package com.ruoyi.manage.domain;

import com.ruoyi.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI濞戝牊浼呯€电钖?fe_ai_message
 */
public class FeAiMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long messageId;

    private String conversationId;

    private Long userId;

    private String role;

    private String content;

    private String answerType;

    private String responseType;

    private String status;

    private Long elapsedMs;

    private Integer sortNo;

    private String delFlag;

    public Long getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Long messageId)
    {
        this.messageId = messageId;
    }

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

    public String getRole()
    {
        return role;
    }

    public void setRole(String role)
    {
        this.role = role;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public String getAnswerType()
    {
        return answerType;
    }

    public void setAnswerType(String answerType)
    {
        this.answerType = answerType;
    }

    public String getResponseType()
    {
        return responseType;
    }

    public void setResponseType(String responseType)
    {
        this.responseType = responseType;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getElapsedMs()
    {
        return elapsedMs;
    }

    public void setElapsedMs(Long elapsedMs)
    {
        this.elapsedMs = elapsedMs;
    }

    public Integer getSortNo()
    {
        return sortNo;
    }

    public void setSortNo(Integer sortNo)
    {
        this.sortNo = sortNo;
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
            .append("messageId", getMessageId())
            .append("conversationId", getConversationId())
            .append("userId", getUserId())
            .append("role", getRole())
            .append("content", getContent())
            .append("answerType", getAnswerType())
            .append("responseType", getResponseType())
            .append("status", getStatus())
            .append("elapsedMs", getElapsedMs())
            .append("sortNo", getSortNo())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
