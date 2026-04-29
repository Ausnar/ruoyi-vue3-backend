package com.ruoyi.manage.domain.report;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 报表风险明细。
 */
public class DeviceReportRiskItem
{
    private String category;
    private String riskType;
    private String riskLevel;
    private String deptName;
    private String itemName;
    private String itemCode;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date occurTime;

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getRiskType() { return riskType; }
    public void setRiskType(String riskType) { this.riskType = riskType; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getOccurTime() { return occurTime; }
    public void setOccurTime(Date occurTime) { this.occurTime = occurTime; }
}
