package com.ruoyi.manage.controller;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.manage.config.CozeConfig;

/**
 * 智能问答Controller（最终修复版）
 *
 * @author ruoyi
 * @date 2026-03-10
 */
@RestController
@RequestMapping("/robot")
public class RobotController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(RobotController.class);

    @Autowired
    private CozeConfig cozeConfig;

    // 使用成员变量复用 RestTemplate
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送消息获取AI回复
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, String> request)
    {
        String message = request.get("message");
        log.info("【智能问答】收到前端请求，消息内容：{}", message);

        if (message == null || message.trim().isEmpty()) {
            return error("消息不能为空");
        }

        try {
            // 1. 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + cozeConfig.getApiKey());

            // 2. 构建V3 API请求体
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

            log.info("【智能问答】发送给Coze(V3)的请求体：{}", body);

            // 3. 第一步：创建会话
            String apiUrl = cozeConfig.getApiUrl();
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> createResponse = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                requestEntity,
                Map.class
            );

            Map<String, Object> createResponseBody = createResponse.getBody();
            log.info("【智能问答】创建会话响应：{}", createResponseBody);

            if (createResponseBody == null) {
                return error("创建会话失败：响应为空");
            }
            String code = createResponseBody.get("code") != null ? createResponseBody.get("code").toString() : null;
            if (!"0".equals(code)) {
                String errorMsg = createResponseBody.get("msg") != null ? createResponseBody.get("msg").toString() : "创建会话失败";
                return error(errorMsg);
            }

            // 4. 第二步：获取会话ID
            Map<String, Object> data = (Map<String, Object>) createResponseBody.get("data");
            if (data == null || data.get("conversation_id") == null || data.get("id") == null) {
                return error("获取会话ID失败：响应数据不完整");
            }
            String conversationId = data.get("conversation_id").toString();
            String chatId = data.get("id").toString();

            log.info("【智能问答】会话创建成功，conversationId={}, chatId={}", conversationId, chatId);

            // 5. 第三步：等待并轮询结果
            String replyContent = waitAndGetResult(restTemplate, headers, conversationId, chatId);
            log.info("【智能问答】最终提取到的AI回复：{}", replyContent);

            return success(replyContent);
        } catch (Exception e) {
            log.error("【智能问答】调用AI服务异常", e);
            return error("调用AI服务失败: " + e.getMessage());
        }
    }

    /**
     * 等待并获取AI回复
     */
    private String waitAndGetResult(RestTemplate restTemplate, HttpHeaders headers, String conversationId, String chatId) {
        int maxRetries = 30;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                Thread.sleep(1500);
                
                // 检查状态
                String retrieveUrl = String.format("https://api.coze.cn/v3/chat/retrieve?conversation_id=%s&chat_id=%s", 
                    conversationId, chatId);
                
                HttpEntity<Void> retrieveRequest = new HttpEntity<>(headers);
                ResponseEntity<Map> retrieveResponse = restTemplate.exchange(
                    retrieveUrl,
                    HttpMethod.GET,
                    retrieveRequest,
                    Map.class
                );

                Map<String, Object> retrieveBody = retrieveResponse.getBody();
                log.info("【智能问答】状态检查（第{}次）：{}", retryCount + 1, retrieveBody);

                if (retrieveBody != null && "0".equals(retrieveBody.get("code") != null ? retrieveBody.get("code").toString() : null)) {
                    Map<String, Object> data = (Map<String, Object>) retrieveBody.get("data");
                    if (data == null || data.get("status") == null) {
                        retryCount++;
                        continue;
                    }
                    String status = data.get("status").toString();
                    
                    if ("completed".equals(status)) {
                        return getMessageList(restTemplate, headers, conversationId, chatId);
                    }
                    
                    if ("failed".equals(status)) {
                        Map<String, Object> lastError = (Map<String, Object>) data.get("last_error");
                        return "AI回复失败：" + (lastError != null ? lastError.get("msg") : "未知错误");
                    }
                }
                
                retryCount++;
            } catch (Exception e) {
                log.error("【智能问答】轮询异常（第{}次）", retryCount + 1, e);
                retryCount++;
            }
        }
        
        return "抱歉，AI回复超时，请稍后重试。";
    }

    /**
     * ✅ 修复类型转换错误：data 是 List 不是 Map
     */
    private String getMessageList(RestTemplate restTemplate, HttpHeaders headers, String conversationId, String chatId) {
        try {
            String messageListUrl = String.format("https://api.coze.cn/v3/chat/message/list?conversation_id=%s&chat_id=%s",
                conversationId, chatId);
            
            HttpEntity<Void> messageRequest = new HttpEntity<>(headers);
            ResponseEntity<Map> messageResponse = restTemplate.exchange(
                messageListUrl,
                HttpMethod.GET,
                messageRequest,
                Map.class
            );

            Map<String, Object> messageBody = messageResponse.getBody();
            log.info("【智能问答】消息列表响应：{}", messageBody);

            if (messageBody != null && "0".equals(messageBody.get("code") != null ? messageBody.get("code").toString() : null)) {
                // ✅ 关键修复：data 是 List 类型，不是 Map
                List<Map<String, Object>> messages = (List<Map<String, Object>>) messageBody.get("data");
                if (messages != null) {
                    for (Map<String, Object> msg : messages) {
                        if (msg != null && "assistant".equals(msg.get("role")) && "answer".equals(msg.get("type"))) {
                            return msg.get("content") != null ? msg.get("content").toString() : "";
                        }
                    }
                }
            }
            return "抱歉，我暂时无法回答这个问题。";
        } catch (Exception e) {
            log.error("【智能问答】获取消息列表异常", e);
            return "解析响应失败: " + e.getMessage();
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            return 0L;
        } catch (Exception e) {
            return 0L;
        }
    }
}