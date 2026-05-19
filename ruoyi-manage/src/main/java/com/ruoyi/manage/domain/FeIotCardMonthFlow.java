package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeIotCardMonthFlow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long flowId;
    private Long cardId;
    private String iccid;
    private String flowMonth;
    private BigDecimal monthUsedFlow;
    private String rawResponse;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date syncTime;

    public Long getFlowId() { return flowId; }
    public void setFlowId(Long flowId) { this.flowId = flowId; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public String getFlowMonth() { return flowMonth; }
    public void setFlowMonth(String flowMonth) { this.flowMonth = flowMonth; }
    public BigDecimal getMonthUsedFlow() { return monthUsedFlow; }
    public void setMonthUsedFlow(BigDecimal monthUsedFlow) { this.monthUsedFlow = monthUsedFlow; }
    public String getRawResponse() { return rawResponse; }
    public void setRawResponse(String rawResponse) { this.rawResponse = rawResponse; }
    public Date getSyncTime() { return syncTime; }
    public void setSyncTime(Date syncTime) { this.syncTime = syncTime; }
}
