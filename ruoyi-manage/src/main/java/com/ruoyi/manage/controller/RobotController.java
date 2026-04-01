package com.ruoyi.manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.config.CozeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能问答 Controller
 */
@RestController
@RequestMapping("/robot")
public class RobotController extends BaseController {

    private static final Logger log = LoggerFactory.getLogger(RobotController.class);

    @Autowired
    private CozeConfig cozeConfig;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送消息并获取 AI 回复
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        String requestConversationId = request.get("conversationId");
        boolean isContinueConversation = requestConversationId != null && !requestConversationId.trim().isEmpty();

        log.info("[Robot] request received, conversationId={}, message={}", requestConversationId, message);

        if (message == null || message.trim().isEmpty()) {
            return error("消息不能为空");
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cozeConfig.getApiKey());

            Map<String, Object> body = new HashMap<>();
            body.put("bot_id", cozeConfig.getBotId());
            body.put("user_id", "user_" + getCurrentUserId());
            body.put("stream", false);
            body.put("auto_save_history", true);

            List<Map<String, Object>> messages = new ArrayList<>();
            Map<String, Object> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            userMessage.put("content_type", "text");
            messages.add(userMessage);
            body.put("additional_messages", messages);

            String apiUrl = buildChatApiUrl(cozeConfig.getApiUrl(), requestConversationId);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> createResponse = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, Map.class);

            Map<String, Object> createResponseBody = createResponse.getBody();
            log.info("[Robot] create chat response={}", createResponseBody);

            if (createResponseBody == null) {
                return error("创建会话失败：响应为空");
            }

            String code = createResponseBody.get("code") != null ? createResponseBody.get("code").toString() : null;
            if (!"0".equals(code)) {
                String errorMsg = createResponseBody.get("msg") != null
                    ? createResponseBody.get("msg").toString()
                    : "创建会话失败";
                return error(errorMsg);
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) createResponseBody.get("data");
            if (data == null || data.get("id") == null) {
                return error("获取会话信息失败：响应数据不完整");
            }

            String conversationId = data.get("conversation_id") != null ? data.get("conversation_id").toString() : null;
            if ((conversationId == null || conversationId.trim().isEmpty()) && isContinueConversation) {
                conversationId = requestConversationId;
            }

            String chatId = data.get("id").toString();

            log.info("[Robot] session created, conversationId={}, chatId={}", conversationId, chatId);
            if (isContinueConversation && conversationId != null && !requestConversationId.equals(conversationId)) {
                log.warn("[Robot] conversation mismatch, requestConversationId={}, responseConversationId={}",
                    requestConversationId, conversationId);
            }

            String replyContent = waitAndGetResult(headers, conversationId, chatId);
            log.info("[Robot] final reply length={}", replyContent != null ? replyContent.length() : 0);

            Map<String, Object> result = new HashMap<>();
            result.put("reply", replyContent);
            result.put("conversationId", conversationId);
            return success(result);
        } catch (Exception e) {
            log.error("[Robot] chat request failed", e);
            return error("调用AI服务失败: " + e.getMessage());
        }
    }

    /**
     * 轮询等待结果
     */
    private String waitAndGetResult(HttpHeaders headers, String conversationId, String chatId) {
        int maxRetries = 120;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                long sleepMs = retryCount < 5 ? 500 : 1500;
                Thread.sleep(sleepMs);

                String retrieveUrl = String.format(
                    "https://api.coze.cn/v3/chat/retrieve?conversation_id=%s&chat_id=%s",
                    conversationId, chatId
                );

                HttpEntity<Void> retrieveRequest = new HttpEntity<>(headers);
                ResponseEntity<Map> retrieveResponse = restTemplate.exchange(
                    retrieveUrl,
                    HttpMethod.GET,
                    retrieveRequest,
                    Map.class
                );

                Map<String, Object> retrieveBody = retrieveResponse.getBody();
                if (retryCount == 0 || retryCount % 20 == 0) {
                    log.info("[Robot] poll status, retryCount={}, body={}", retryCount, retrieveBody);
                }

                if (retrieveBody != null && "0".equals(stringify(retrieveBody.get("code")))) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) retrieveBody.get("data");
                    if (data == null || data.get("status") == null) {
                        retryCount++;
                        continue;
                    }

                    String status = data.get("status").toString();
                    if ("completed".equals(status)) {
                        return getMessageList(headers, conversationId, chatId);
                    }

                    if ("failed".equals(status)) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> lastError = (Map<String, Object>) data.get("last_error");
                        return "AI回复失败：" + (lastError != null ? stringify(lastError.get("msg")) : "未知错误");
                    }
                }

                retryCount++;
            } catch (Exception e) {
                log.error("[Robot] poll failed, retryCount={}", retryCount, e);
                retryCount++;
            }
        }

        return "抱歉，AI回复超时，请稍后重试。";
    }

    /**
     * 获取最终消息列表并提取可读内容
     */
    private String getMessageList(HttpHeaders headers, String conversationId, String chatId) {
        try {
            String messageListUrl = String.format(
                "https://api.coze.cn/v3/chat/message/list?conversation_id=%s&chat_id=%s",
                conversationId, chatId
            );

            HttpEntity<Void> messageRequest = new HttpEntity<>(headers);
            ResponseEntity<Map> messageResponse = restTemplate.exchange(
                messageListUrl,
                HttpMethod.GET,
                messageRequest,
                Map.class
            );

            Map<String, Object> messageBody = messageResponse.getBody();
            log.info("[Robot] message list response={}", messageBody);

            if (messageBody != null && "0".equals(stringify(messageBody.get("code")))) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> messages = (List<Map<String, Object>>) messageBody.get("data");
                if (messages != null) {
                    String bestAnswer = null;
                    List<String> assistantOutputs = new ArrayList<>();

                    for (Map<String, Object> msg : messages) {
                        if (msg == null || !"assistant".equals(stringify(msg.get("role")))) {
                            continue;
                        }

                        String parsed = parseAssistantMessage(msg);
                        if (parsed == null || parsed.trim().isEmpty()) {
                            continue;
                        }

                        assistantOutputs.add(parsed);
                        if ("answer".equals(stringify(msg.get("type"))) && bestAnswer == null) {
                            bestAnswer = parsed;
                        }
                    }

                    if (bestAnswer != null && !bestAnswer.trim().isEmpty()) {
                        return bestAnswer;
                    }
                    if (!assistantOutputs.isEmpty()) {
                        return String.join("\n\n", assistantOutputs);
                    }
                }
            }

            return "抱歉，我暂时无法回答这个问题。";
        } catch (Exception e) {
            log.error("[Robot] get message list failed", e);
            return "解析响应失败: " + e.getMessage();
        }
    }

    /**
     * 解析 assistant 消息，尽量保留 markdown / 卡片信息
     */
    private String parseAssistantMessage(Map<String, Object> msg) {
        String content = stringify(msg.get("content"));
        if (content.trim().isEmpty()) {
            return "";
        }

        String responseType = stringify(msg.get("response_type"));
        String contentType = stringify(msg.get("content_type"));

        if ("text".equals(contentType) || "markdown".equalsIgnoreCase(responseType)) {
            return content;
        }

        boolean mayBeCard = "card".equalsIgnoreCase(responseType) || content.trim().startsWith("{");
        if (!mayBeCard) {
            return content;
        }

        String parsed = parseCardContent(content);
        return parsed == null || parsed.trim().isEmpty() ? content : parsed;
    }

    /**
     * 解析卡片内容，输出 markdown 文本
     */
    @SuppressWarnings("unchecked")
    private String parseCardContent(String content) {
        try {
            Map<String, Object> card = objectMapper.readValue(content, Map.class);

            Object msgObj = card.get("msg");
            if (msgObj instanceof String) {
                String cardMsg = msgObj.toString();
                if (!cardMsg.trim().isEmpty() && !cardMsg.trim().startsWith("{")) {
                    return cardMsg;
                }
            }

            Object dataObj = card.get("data");
            if (!(dataObj instanceof String)) {
                return "";
            }

            Map<String, Object> dataMap = objectMapper.readValue(dataObj.toString(), Map.class);
            Object varsObj = dataMap.get("variables");
            if (!(varsObj instanceof Map)) {
                return "";
            }

            Map<String, Object> vars = (Map<String, Object>) varsObj;
            for (Object varVal : vars.values()) {
                if (!(varVal instanceof Map)) continue;
                Object defaultValue = ((Map<String, Object>) varVal).get("defaultValue");
                if (defaultValue instanceof List) {
                    return formatCardItems((List<Map<String, Object>>) defaultValue);
                }
            }

            return "";
        } catch (Exception e) {
            log.warn("[Robot] parse card content failed: {}", e.getMessage());
            return "";
        }
    }

    private String formatCardItems(List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : items) {
            if (item == null) continue;

            String index = stringify(item.get("index"));
            String title = stringify(item.get("title"));
            String link = stringify(item.get("link"));
            String desc = !stringify(item.get("con")).isEmpty()
                ? stringify(item.get("con"))
                : stringify(item.get("desc"));

            if (!title.isEmpty()) {
                sb.append(index.isEmpty() ? "- " : index + ". ");
                if (!link.isEmpty()) {
                    sb.append("[").append(title).append("](").append(link).append(")");
                } else {
                    sb.append(title);
                }
                sb.append("\n");
            }

            if (!desc.isEmpty()) {
                sb.append(desc).append("\n\n");
            }
        }
        return sb.toString().trim();
    }

    private Long getCurrentUserId() {
        try {
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }

    /**
     * 续聊时 conversation_id 放在 query 参数
     */
    private String buildChatApiUrl(String baseApiUrl, String conversationId) {
        if (baseApiUrl == null || baseApiUrl.trim().isEmpty()) {
            return "https://api.coze.cn/v3/chat";
        }
        if (conversationId == null || conversationId.trim().isEmpty()) {
            return baseApiUrl;
        }

        String encodedConversationId;
        try {
            encodedConversationId = URLEncoder.encode(conversationId.trim(), StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            log.warn("[Robot] encode conversation_id failed, use raw value. conversationId={}", conversationId);
            encodedConversationId = conversationId.trim();
        }

        return baseApiUrl.contains("?")
            ? baseApiUrl + "&conversation_id=" + encodedConversationId
            : baseApiUrl + "?conversation_id=" + encodedConversationId;
    }

    private String stringify(Object obj) {
        return obj == null ? "" : obj.toString();
    }
}
