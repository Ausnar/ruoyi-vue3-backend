package com.ruoyi.manage.domain.report;

import java.util.ArrayList;
import java.util.List;

/**
 * 设备报告页面预览结果。
 */
public class DeviceReportPreview
{
    private String reportType;
    private String reportTypeName;
    private String periodText;
    private String generatedTime;
    private String scopeName;
    private DeviceReportOverview overview = new DeviceReportOverview();
    private List<DeviceReportUnitStat> unitStats = new ArrayList<>();
    private List<DeviceReportRiskItem> riskItems = new ArrayList<>();

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public String getReportTypeName() { return reportTypeName; }
    public void setReportTypeName(String reportTypeName) { this.reportTypeName = reportTypeName; }
    public String getPeriodText() { return periodText; }
    public void setPeriodText(String periodText) { this.periodText = periodText; }
    public String getGeneratedTime() { return generatedTime; }
    public void setGeneratedTime(String generatedTime) { this.generatedTime = generatedTime; }
    public String getScopeName() { return scopeName; }
    public void setScopeName(String scopeName) { this.scopeName = scopeName; }
    public DeviceReportOverview getOverview() { return overview; }
    public void setOverview(DeviceReportOverview overview) { this.overview = overview; }
    public List<DeviceReportUnitStat> getUnitStats() { return unitStats; }
    public void setUnitStats(List<DeviceReportUnitStat> unitStats) { this.unitStats = unitStats; }
    public List<DeviceReportRiskItem> getRiskItems() { return riskItems; }
    public void setRiskItems(List<DeviceReportRiskItem> riskItems) { this.riskItems = riskItems; }
}
