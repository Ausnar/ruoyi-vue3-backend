package com.ruoyi.manage.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * Coze API 配置类
 *
 * @author ruoyi
 * @date 2026-03-10
 */
@Component
public class CozeConfig
{
    private static final Logger log = LoggerFactory.getLogger(CozeConfig.class);

    /**
     * Coze API Key
     */
    @Value("${coze.apiKey:}")
    private String apiKey;

    /**
     * Coze Bot ID
     */
    @Value("${coze.botId:}")
    private String botId;

    /**
     * Coze API 地址
     */
    @Value("${coze.apiUrl:https://api.coze.cn/v3/chat}")
    private String apiUrl;

    /**
     * 启动时打印配置（用于排查问题）
     */
    @PostConstruct
    public void printConfig() {
        log.info("========== Coze 配置加载结果 ==========");
        log.info("apiKey: [{}]", apiKey);
        log.info("apiKey长度: {}", apiKey != null ? apiKey.length() : 0);
        log.info("botId: [{}]", botId);
        log.info("apiUrl: [{}]", apiUrl);
        log.info("======================================");
    }

    public String getApiKey()
    {
        return apiKey;
    }

    public void setApiKey(String apiKey)
    {
        this.apiKey = apiKey;
    }

    public String getBotId()
    {
        return botId;
    }

    public void setBotId(String botId)
    {
        this.botId = botId;
    }

    public String getApiUrl()
    {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl)
    {
        this.apiUrl = apiUrl;
    }
}