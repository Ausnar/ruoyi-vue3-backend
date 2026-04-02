package com.ruoyi.manage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.config.CozeConfig;
import com.ruoyi.manage.domain.FeAiConversation;
import com.ruoyi.manage.domain.FeAiMessage;
import com.ruoyi.manage.service.IRobotConversationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/robot")
public class RobotController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(RobotController.class);
    private static final String DEFAULT_CHAT_API = "https://api.coze.cn/v3/chat";
    private static final String DEFAULT_REPLY = "抱歉，我暂时无法整理出可读的回复，请稍后重试。";
    private static final int MAX_POLL_RETRIES = 120;

    @Autowired
    private CozeConfig cozeConfig;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IRobotConversationService robotConversationService;

    private final RestTemplate restTemplate = createRestTemplate();

    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, String> request)
    {
        String message = normalize(request.get("message"));
        String requestConversationId = normalize(request.get("conversationId"));
        boolean continueConversation = !requestConversationId.isEmpty();
        long startedAt = System.currentTimeMillis();

        if (message.isEmpty())
        {
            return error("消息不能为空");
        }
        if (normalize(cozeConfig.getApiKey()).isEmpty() || normalize(cozeConfig.getBotId()).isEmpty())
        {
            return error("Coze 配置不完整，请先检查 apiKey 和 botId");
        }

        Long userId = resolveCurrentUserId();
        String username = resolveCurrentUsername();
        log.info("[Robot] request received, userId={}, continueConversation={}, conversationId={}, messageLength={}, preview={}",
            userId, continueConversation, requestConversationId, message.length(), preview(message));

        try
        {
            HttpHeaders headers = buildHeaders();
            Map<String, Object> body = buildChatBody(message, userId);
            String apiUrl = buildChatApiUrl(cozeConfig.getApiUrl(), requestConversationId);

            ResponseEntity<Map> createResponse = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                Map.class
            );

            Map<String, Object> createResponseBody = createResponse.getBody();
            log.info("[Robot] create response code={}, body={}", createResponse.getStatusCodeValue(), createResponseBody);

            if (createResponseBody == null)
            {
                return error("创建会话失败：响应为空");
            }

            String code = normalize(createResponseBody.get("code"));
            if (!"0".equals(code))
            {
                return error(normalize(createResponseBody.get("msg"), "创建会话失败"));
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) createResponseBody.get("data");
            if (data == null || data.get("id") == null)
            {
                return error("获取会话信息失败：响应数据不完整");
            }

            String conversationId = normalize(data.get("conversation_id"));
            if (conversationId.isEmpty())
            {
                conversationId = requestConversationId;
            }
            String chatId = normalize(data.get("id"));

            log.info("[Robot] session created, userId={}, requestConversationId={}, responseConversationId={}, chatId={}",
                userId, requestConversationId, conversationId, chatId);

            ChatAnswer answer = waitAndGetResult(headers, conversationId, chatId);
            long elapsedMs = System.currentTimeMillis() - startedAt;

            try
            {
                robotConversationService.saveChatHistory(userId, username, conversationId, chatId, message,
                    answer.content, answer.answerType, answer.responseType, answer.status, elapsedMs);
            }
            catch (Exception saveException)
            {
                log.error("[Robot] save chat history failed, conversationId={}", conversationId, saveException);
            }

            Map<String, Object> result = new HashMap<>();
            result.put("reply", answer.content);
            result.put("conversationId", conversationId);
            result.put("answerType", answer.answerType);
            result.put("responseType", answer.responseType);
            result.put("status", answer.status);
            result.put("continueConversation", continueConversation);
            result.put("elapsedMs", elapsedMs);
            result.put("canRetry", answer.retryable);

            log.info("[Robot] completed, userId={}, conversationId={}, answerType={}, responseType={}, status={}, elapsedMs={}, replyLength={}",
                userId, conversationId, answer.answerType, answer.responseType, answer.status, elapsedMs, answer.content.length());
            return success(result);
        }
        catch (Exception e)
        {
            log.error("[Robot] chat request failed", e);
            return error("调用 AI 服务失败: " + e.getMessage());
        }
    }

    @GetMapping("/conversations")
    public AjaxResult conversations()
    {
        Long userId = resolveCurrentUserId();
        return success(robotConversationService.selectConversationList(userId));
    }

    @GetMapping("/conversations/{conversationId}")
    public AjaxResult conversationDetail(@PathVariable("conversationId") String conversationId)
    {
        Long userId = resolveCurrentUserId();
        FeAiConversation conversation = robotConversationService.selectConversation(conversationId, userId);
        if (conversation == null)
        {
            return error("会话不存在或无权访问");
        }

        List<FeAiMessage> messages = robotConversationService.selectMessageList(conversationId, userId);
        Map<String, Object> data = new HashMap<>();
        data.put("conversation", conversation);
        data.put("messages", messages);
        return success(data);
    }

    @DeleteMapping("/conversations/{conversationId}")
    public AjaxResult deleteConversation(@PathVariable("conversationId") String conversationId)
    {
        Long userId = resolveCurrentUserId();
        String username = resolveCurrentUsername();
        FeAiConversation conversation = robotConversationService.selectConversation(conversationId, userId);
        if (conversation == null)
        {
            return error("会话不存在或无权删除");
        }
        return toAjax(robotConversationService.deleteConversation(conversationId, userId, username));
    }

    private HttpHeaders buildHeaders()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + cozeConfig.getApiKey());
        return headers;
    }

    private Map<String, Object> buildChatBody(String message, Long userId)
    {
        Map<String, Object> body = new HashMap<>();
        body.put("bot_id", cozeConfig.getBotId());
        body.put("user_id", "user_" + userId);
        body.put("stream", false);
        body.put("auto_save_history", true);

        List<Map<String, Object>> additionalMessages = new ArrayList<>();
        Map<String, Object> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", message);
        userMessage.put("content_type", "text");
        additionalMessages.add(userMessage);
        body.put("additional_messages", additionalMessages);
        return body;
    }

    private ChatAnswer waitAndGetResult(HttpHeaders headers, String conversationId, String chatId)
    {
        int retryCount = 0;
        String latestStatus = "in_progress";

        while (retryCount < MAX_POLL_RETRIES)
        {
            try
            {
                Thread.sleep(retryCount < 5 ? 500L : 1500L);
                String retrieveUrl = String.format(
                    "https://api.coze.cn/v3/chat/retrieve?conversation_id=%s&chat_id=%s",
                    conversationId, chatId
                );

                ResponseEntity<Map> retrieveResponse = restTemplate.exchange(
                    retrieveUrl,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
                );
                Map<String, Object> retrieveBody = retrieveResponse.getBody();

                if (retryCount == 0 || retryCount % 20 == 0)
                {
                    log.info("[Robot] poll status, retryCount={}, body={}", retryCount, retrieveBody);
                }

                if (retrieveBody != null && "0".equals(normalize(retrieveBody.get("code"))))
                {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) retrieveBody.get("data");
                    latestStatus = normalize(data != null ? data.get("status") : null, latestStatus);

                    if ("completed".equals(latestStatus))
                    {
                        return getMessageList(headers, conversationId, chatId);
                    }
                    if ("failed".equals(latestStatus) || "cancelled".equals(latestStatus))
                    {
                        String errorMsg = "AI 回复失败，请稍后重试。";
                        if (data != null && data.get("last_error") instanceof Map)
                        {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> lastError = (Map<String, Object>) data.get("last_error");
                            errorMsg = normalize(lastError.get("msg"), errorMsg);
                        }
                        return ChatAnswer.failed(errorMsg, latestStatus);
                    }
                }
                retryCount++;
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                return ChatAnswer.failed("对话被中断，请重新发送问题。", latestStatus);
            }
            catch (Exception e)
            {
                log.error("[Robot] poll failed, retryCount={}", retryCount, e);
                retryCount++;
            }
        }

        return ChatAnswer.timeout();
    }

    private ChatAnswer getMessageList(HttpHeaders headers, String conversationId, String chatId)
    {
        try
        {
            String messageListUrl = String.format(
                "https://api.coze.cn/v3/chat/message/list?conversation_id=%s&chat_id=%s",
                conversationId, chatId
            );

            ResponseEntity<Map> messageResponse = restTemplate.exchange(
                messageListUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Map.class
            );
            Map<String, Object> messageBody = messageResponse.getBody();
            log.info("[Robot] message list response={}", messageBody);

            if (messageBody != null && "0".equals(normalize(messageBody.get("code"))))
            {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> messages = (List<Map<String, Object>>) messageBody.get("data");
                if (messages != null && !messages.isEmpty())
                {
                    ChatAnswer preferredAnswer = null;
                    ChatAnswer latestUsefulAnswer = null;
                    List<String> fragments = new ArrayList<>();

                    for (Map<String, Object> msg : messages)
                    {
                        if (msg == null || !"assistant".equals(normalize(msg.get("role"))))
                        {
                            continue;
                        }

                        ChatAnswer parsed = parseAssistantMessage(msg);
                        if (parsed == null || parsed.content.isEmpty())
                        {
                            continue;
                        }

                        fragments.add(parsed.content);
                        latestUsefulAnswer = parsed;
                        if ("answer".equals(normalize(msg.get("type"))))
                        {
                            preferredAnswer = parsed;
                        }
                    }

                    if (preferredAnswer != null)
                    {
                        preferredAnswer.status = "completed";
                        return preferredAnswer;
                    }
                    if (latestUsefulAnswer != null)
                    {
                        latestUsefulAnswer.status = "completed";
                        if (fragments.size() > 1)
                        {
                            latestUsefulAnswer.content = joinUniqueLines(fragments);
                        }
                        return latestUsefulAnswer;
                    }
                }
            }
            return ChatAnswer.failed(DEFAULT_REPLY, "completed");
        }
        catch (Exception e)
        {
            log.error("[Robot] get message list failed", e);
            return ChatAnswer.failed("解析响应失败: " + e.getMessage(), "completed");
        }
    }

    private ChatAnswer parseAssistantMessage(Map<String, Object> msg)
    {
        String content = normalize(msg.get("content"));
        if (content.isEmpty())
        {
            return null;
        }

        String type = normalize(msg.get("type"), "answer");
        String responseType = normalize(msg.get("response_type"));
        String contentType = normalize(msg.get("content_type"));

        if ("text".equals(contentType) || "markdown".equalsIgnoreCase(responseType))
        {
            return ChatAnswer.success(content, type, normalize(responseType, contentType, "text"));
        }

        if ("card".equalsIgnoreCase(responseType) || looksLikeJson(content))
        {
            String parsed = parseCardContent(content);
            if (!parsed.isEmpty())
            {
                return ChatAnswer.success(parsed, type, normalize(responseType, "card"));
            }
            return null;
        }

        return ChatAnswer.success(content, type, normalize(responseType, contentType, "text"));
    }

    private String parseCardContent(String content)
    {
        try
        {
            Object parsed = objectMapper.readValue(content, Object.class);
            return extractReadableText(parsed);
        }
        catch (Exception e)
        {
            log.warn("[Robot] parse card content failed: {}", e.getMessage());
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractReadableText(Object node)
    {
        if (node == null)
        {
            return "";
        }
        if (node instanceof String)
        {
            String text = normalize(node);
            if (text.isEmpty())
            {
                return "";
            }
            if (looksLikeJson(text))
            {
                try
                {
                    return extractReadableText(objectMapper.readValue(text, Object.class));
                }
                catch (Exception ignore)
                {
                    return "";
                }
            }
            return looksReadable(text) ? text : "";
        }
        if (node instanceof List)
        {
            List<?> list = (List<?>) node;
            if (!list.isEmpty() && list.get(0) instanceof Map)
            {
                String formatted = formatCardItems((List<Map<String, Object>>) list);
                if (!formatted.isEmpty())
                {
                    return formatted;
                }
            }
            List<String> parts = new ArrayList<>();
            for (Object item : list)
            {
                String text = extractReadableText(item);
                if (!text.isEmpty())
                {
                    parts.add(text);
                }
            }
            return joinUniqueLines(parts);
        }
        if (!(node instanceof Map))
        {
            return "";
        }

        Map<String, Object> map = (Map<String, Object>) node;
        Set<String> texts = new LinkedHashSet<>();

        String preferredText = firstReadable(map, "msg", "markdown", "text", "answer", "output", "content");
        if (!preferredText.isEmpty())
        {
            texts.add(preferredText);
        }

        Object dataObj = map.get("data");
        if (dataObj != null)
        {
            String dataText = extractReadableText(dataObj);
            if (!dataText.isEmpty())
            {
                texts.add(dataText);
            }
        }

        Object variablesObj = map.get("variables");
        if (variablesObj instanceof Map)
        {
            for (Object variable : ((Map<String, Object>) variablesObj).values())
            {
                if (variable instanceof Map)
                {
                    Object defaultValue = ((Map<String, Object>) variable).get("defaultValue");
                    String variableText = extractReadableText(defaultValue);
                    if (!variableText.isEmpty())
                    {
                        texts.add(variableText);
                    }
                }
            }
        }

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            String key = normalize(entry.getKey());
            if ("msg".equals(key) || "markdown".equals(key) || "text".equals(key)
                || "answer".equals(key) || "output".equals(key) || "content".equals(key)
                || "data".equals(key) || "variables".equals(key))
            {
                continue;
            }
            String nestedText = extractReadableText(entry.getValue());
            if (!nestedText.isEmpty())
            {
                texts.add(nestedText);
            }
        }

        return joinUniqueLines(new ArrayList<>(texts));
    }

    private String formatCardItems(List<Map<String, Object>> items)
    {
        if (items == null || items.isEmpty())
        {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> item : items)
        {
            if (item == null)
            {
                continue;
            }

            String index = normalize(item.get("index"));
            String title = normalize(item.get("title"));
            String link = normalize(item.get("link"));
            String desc = normalize(item.get("con"), item.get("desc"), item.get("summary"), item.get("content"));

            if (!title.isEmpty())
            {
                sb.append(index.isEmpty() ? "- " : index + ". ");
                if (!link.isEmpty())
                {
                    sb.append("[").append(title).append("](").append(link).append(")");
                }
                else
                {
                    sb.append(title);
                }
                sb.append("\n");
            }

            if (!desc.isEmpty())
            {
                sb.append(desc).append("\n\n");
            }
        }
        return sb.toString().trim();
    }

    private RestTemplate createRestTemplate()
    {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(60000);
        return new RestTemplate(factory);
    }

    private Long resolveCurrentUserId()
    {
        try
        {
            Long userId = getUserId();
            return userId != null ? userId : 0L;
        }
        catch (Exception e)
        {
            log.warn("[Robot] resolve userId failed, fallback to anonymous", e);
            return 0L;
        }
    }

    private String resolveCurrentUsername()
    {
        try
        {
            String username = getUsername();
            return username == null || username.trim().isEmpty() ? "system" : username.trim();
        }
        catch (Exception e)
        {
            log.warn("[Robot] resolve username failed, fallback to system", e);
            return "system";
        }
    }

    private String buildChatApiUrl(String baseApiUrl, String conversationId)
    {
        String apiUrl = normalize(baseApiUrl, DEFAULT_CHAT_API);
        if (conversationId.isEmpty())
        {
            return apiUrl;
        }

        String encodedConversationId;
        try
        {
            encodedConversationId = URLEncoder.encode(conversationId, StandardCharsets.UTF_8.name());
        }
        catch (Exception e)
        {
            log.warn("[Robot] encode conversation_id failed, use raw value. conversationId={}", conversationId);
            encodedConversationId = conversationId;
        }

        return apiUrl.contains("?")
            ? apiUrl + "&conversation_id=" + encodedConversationId
            : apiUrl + "?conversation_id=" + encodedConversationId;
    }

    private String preview(String text)
    {
        String normalized = normalize(text).replaceAll("\\s+", " ");
        return normalized.length() <= 60 ? normalized : normalized.substring(0, 60) + "...";
    }

    private boolean looksLikeJson(String text)
    {
        String normalized = normalize(text);
        return normalized.startsWith("{") || normalized.startsWith("[");
    }

    private boolean looksReadable(String text)
    {
        String normalized = normalize(text);
        if (normalized.isEmpty() || normalized.length() > 5000)
        {
            return false;
        }
        return normalized.matches(".*[\\u4e00-\\u9fa5A-Za-z].*");
    }

    private String firstReadable(Map<String, Object> map, String... keys)
    {
        for (String key : keys)
        {
            if (!map.containsKey(key))
            {
                continue;
            }
            String text = extractReadableText(map.get(key));
            if (!text.isEmpty())
            {
                return text;
            }
        }
        return "";
    }

    private String joinUniqueLines(List<String> parts)
    {
        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String part : parts)
        {
            String normalized = normalize(part);
            if (!normalized.isEmpty())
            {
                unique.add(normalized);
            }
        }
        return String.join("\n\n", unique).trim();
    }

    private String normalize(Object obj)
    {
        return obj == null ? "" : obj.toString().trim();
    }

    private String normalize(Object obj, String defaultValue)
    {
        String value = normalize(obj);
        return value.isEmpty() ? defaultValue : value;
    }

    private String normalize(Object first, Object second, String defaultValue)
    {
        String firstValue = normalize(first);
        if (!firstValue.isEmpty())
        {
            return firstValue;
        }
        String secondValue = normalize(second);
        return secondValue.isEmpty() ? defaultValue : secondValue;
    }

    private String normalize(Object first, Object second, Object third, Object fourth)
    {
        String[] candidates = {
            normalize(first),
            normalize(second),
            normalize(third),
            normalize(fourth)
        };
        for (String candidate : candidates)
        {
            if (!candidate.isEmpty())
            {
                return candidate;
            }
        }
        return "";
    }

    private static class ChatAnswer
    {
        private String content;
        private final String answerType;
        private final String responseType;
        private String status;
        private final boolean retryable;

        private ChatAnswer(String content, String answerType, String responseType, String status, boolean retryable)
        {
            this.content = content;
            this.answerType = answerType;
            this.responseType = responseType;
            this.status = status;
            this.retryable = retryable;
        }

        private static ChatAnswer success(String content, String answerType, String responseType)
        {
            return new ChatAnswer(content, answerType, responseType, "completed", true);
        }

        private static ChatAnswer failed(String content, String status)
        {
            return new ChatAnswer(content, "error", "text", status, true);
        }

        private static ChatAnswer timeout()
        {
            return new ChatAnswer("当前任务处理时间较长，请稍后继续查看结果，或重新追问更具体的内容。", "timeout", "text", "timeout", true);
        }
    }
}
