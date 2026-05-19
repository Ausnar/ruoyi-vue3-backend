package com.ruoyi.manage.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeIotCard extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long cardId;

    @Excel(name = "ICCID")
    private String iccid;

    @Excel(name = "MSISDN")
    private String msisdn;

    @Excel(name = "服务商账号")
    private String providerAccount;

    @Excel(name = "卡状态")
    private Integer cardStatus;

    @Excel(name = "卡状态名称")
    private String cardStatusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "激活时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date activeTime;

    @Excel(name = "套餐名称")
    private String packageName;

    @Excel(name = "总流量MB")
    private BigDecimal totalFlow;

    @Excel(name = "已用流量MB")
    private BigDecimal usedFlow;

    @Excel(name = "剩余流量MB")
    private BigDecimal leftFlow;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "套餐开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date packageStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "套餐结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date packageStopDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date periodStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date periodStopDate;

    private String period;

    private String currentMonth;

    private BigDecimal monthUsedFlow;

    private String expiryLevel;

    private String flowLevel;

    private String riskSummary;

    private Long gatewayId;

    private String gatewayImei;

    private Long firePointId;

    private String firePointName;

    private Long deptId;

    private String deptName;

    private Long sourceDeptId;

    private String sourceDeptName;

    private String dataSource;

    private String syncStatus;

    private String syncMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date lastSyncTime;

    private String rawCardInfo;

    private String rawPackageInfo;

    private String rawMonthFlow;

    private String delFlag;

    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getIccid() { return iccid; }
    public void setIccid(String iccid) { this.iccid = iccid; }
    public String getMsisdn() { return msisdn; }
    public void setMsisdn(String msisdn) { this.msisdn = msisdn; }
    public String getProviderAccount() { return providerAccount; }
    public void setProviderAccount(String providerAccount) { this.providerAccount = providerAccount; }
    public Integer getCardStatus() { return cardStatus; }
    public void setCardStatus(Integer cardStatus) { this.cardStatus = cardStatus; }
    public String getCardStatusName() { return cardStatusName; }
    public void setCardStatusName(String cardStatusName) { this.cardStatusName = cardStatusName; }
    public Date getActiveTime() { return activeTime; }
    public void setActiveTime(Date activeTime) { this.activeTime = activeTime; }
    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public BigDecimal getTotalFlow() { return totalFlow; }
    public void setTotalFlow(BigDecimal totalFlow) { this.totalFlow = totalFlow; }
    public BigDecimal getUsedFlow() { return usedFlow; }
    public void setUsedFlow(BigDecimal usedFlow) { this.usedFlow = usedFlow; }
    public BigDecimal getLeftFlow() { return leftFlow; }
    public void setLeftFlow(BigDecimal leftFlow) { this.leftFlow = leftFlow; }
    public Date getPackageStartDate() { return packageStartDate; }
    public void setPackageStartDate(Date packageStartDate) { this.packageStartDate = packageStartDate; }
    public Date getPackageStopDate() { return packageStopDate; }
    public void setPackageStopDate(Date packageStopDate) { this.packageStopDate = packageStopDate; }
    public Date getPeriodStartDate() { return periodStartDate; }
    public void setPeriodStartDate(Date periodStartDate) { this.periodStartDate = periodStartDate; }
    public Date getPeriodStopDate() { return periodStopDate; }
    public void setPeriodStopDate(Date periodStopDate) { this.periodStopDate = periodStopDate; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public String getCurrentMonth() { return currentMonth; }
    public void setCurrentMonth(String currentMonth) { this.currentMonth = currentMonth; }
    public BigDecimal getMonthUsedFlow() { return monthUsedFlow; }
    public void setMonthUsedFlow(BigDecimal monthUsedFlow) { this.monthUsedFlow = monthUsedFlow; }
    public String getExpiryLevel() { return expiryLevel; }
    public void setExpiryLevel(String expiryLevel) { this.expiryLevel = expiryLevel; }
    public String getFlowLevel() { return flowLevel; }
    public void setFlowLevel(String flowLevel) { this.flowLevel = flowLevel; }
    public String getRiskSummary() { return riskSummary; }
    public void setRiskSummary(String riskSummary) { this.riskSummary = riskSummary; }
    public Long getGatewayId() { return gatewayId; }
    public void setGatewayId(Long gatewayId) { this.gatewayId = gatewayId; }
    public String getGatewayImei() { return gatewayImei; }
    public void setGatewayImei(String gatewayImei) { this.gatewayImei = gatewayImei; }
    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public Long getSourceDeptId() { return sourceDeptId; }
    public void setSourceDeptId(Long sourceDeptId) { this.sourceDeptId = sourceDeptId; }
    public String getSourceDeptName() { return sourceDeptName; }
    public void setSourceDeptName(String sourceDeptName) { this.sourceDeptName = sourceDeptName; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }
    public String getSyncMessage() { return syncMessage; }
    public void setSyncMessage(String syncMessage) { this.syncMessage = syncMessage; }
    public Date getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(Date lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    public String getRawCardInfo() { return rawCardInfo; }
    public void setRawCardInfo(String rawCardInfo) { this.rawCardInfo = rawCardInfo; }
    public String getRawPackageInfo() { return rawPackageInfo; }
    public void setRawPackageInfo(String rawPackageInfo) { this.rawPackageInfo = rawPackageInfo; }
    public String getRawMonthFlow() { return rawMonthFlow; }
    public void setRawMonthFlow(String rawMonthFlow) { this.rawMonthFlow = rawMonthFlow; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
}
