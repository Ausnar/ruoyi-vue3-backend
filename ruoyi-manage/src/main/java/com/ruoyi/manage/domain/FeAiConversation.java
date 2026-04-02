package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI娴兼俺鐦界€电钖?fe_ai_conversation
 */
public class FeAiConversation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private String conversationId;

    private Long userId;

    private String title;

    private String lastMessagePreview;

    private Integer messageCount;

    private String lastChatId;

    private String status;

    private String delFlag;

    public String getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(String conversationId)
    {
        this.conversationId = conversationId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getLastMessagePreview()
    {
        return lastMessagePreview;
    }

    public void setLastMessagePreview(String lastMessagePreview)
    {
        this.lastMessagePreview = lastMessagePreview;
    }

    public Integer getMessageCount()
    {
        return messageCount;
    }

    public void setMessageCount(Integer messageCount)
    {
        this.messageCount = messageCount;
    }

    public String getLastChatId()
    {
        return lastChatId;
    }

    public void setLastChatId(String lastChatId)
    {
        this.lastChatId = lastChatId;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("conversationId", getConversationId())
            .append("userId", getUserId())
            .append("title", getTitle())
            .append("lastMessagePreview", getLastMessagePreview())
            .append("messageCount", getMessageCount())
            .append("lastChatId", getLastChatId())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
