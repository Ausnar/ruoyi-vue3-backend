package com.ruoyi.manage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "azdaps")
public class AzdapsProperties
{
    private String baseUrl;

    private String loginPath;

    private int connectTimeoutMs = 10000;

    private int readTimeoutMs = 60000;

    private int pageLimit = 100;

    private String historyUnit = "hour";

    public String getBaseUrl()
    {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public String getLoginPath()
    {
        return loginPath;
    }

    public void setLoginPath(String loginPath)
    {
        this.loginPath = loginPath;
    }

    public int getConnectTimeoutMs()
    {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs)
    {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs()
    {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs)
    {
        this.readTimeoutMs = readTimeoutMs;
    }

    public int getPageLimit()
    {
        return pageLimit;
    }

    public void setPageLimit(int pageLimit)
    {
        this.pageLimit = pageLimit;
    }

    public String getHistoryUnit()
    {
        return historyUnit;
    }

    public void setHistoryUnit(String historyUnit)
    {
        this.historyUnit = historyUnit;
    }
}
