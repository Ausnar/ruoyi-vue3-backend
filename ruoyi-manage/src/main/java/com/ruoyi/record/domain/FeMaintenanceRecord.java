package com.ruoyi.record.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 灭火器维护记录对象 fe_maintenance_record
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
public class FeMaintenanceRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long recordId;

    /** 灭火器ID */
    @Excel(name = "灭火器ID")
    private Long extinguisherId;

    /** 标志铭码 */
    @Excel(name = "标志铭码")
    private String labelCode;

    /** 维护类型(检查/维修/更换/充装) */
    @Excel(name = "维护类型(检查/维修/更换/充装)")
    private String maintenanceType;

    /** 维护日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "维护日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date maintenanceDate;

    /** 维护人员 */
    @Excel(name = "维护人员")
    private String maintenancePerson;

    /** 维护单位 */
    @Excel(name = "维护单位")
    private String maintenanceCompany;

    /** 维护结果(合格/不合格) */
    @Excel(name = "维护结果(合格/不合格)")
    private String maintenanceResult;

    /** 问题描述 */
    @Excel(name = "问题描述")
    private String problemDesc;

    /** 解决方案 */
    @Excel(name = "解决方案")
    private String solution;

    /** 维护费用 */
    @Excel(name = "维护费用")
    private BigDecimal cost;

    /** 下次维护日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "下次维护日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date nextMaintenanceDate;

    /** 附件(照片/文档) */
    @Excel(name = "附件(照片/文档)")
    private String attachments;

    public void setRecordId(Long recordId) 
    {
        this.recordId = recordId;
    }

    public Long getRecordId() 
    {
        return recordId;
    }

    public void setExtinguisherId(Long extinguisherId) 
    {
        this.extinguisherId = extinguisherId;
    }

    public Long getExtinguisherId() 
    {
        return extinguisherId;
    }

    public void setLabelCode(String labelCode) 
    {
        this.labelCode = labelCode;
    }

    public String getLabelCode() 
    {
        return labelCode;
    }

    public void setMaintenanceType(String maintenanceType) 
    {
        this.maintenanceType = maintenanceType;
    }

    public String getMaintenanceType() 
    {
        return maintenanceType;
    }

    public void setMaintenanceDate(Date maintenanceDate) 
    {
        this.maintenanceDate = maintenanceDate;
    }

    public Date getMaintenanceDate() 
    {
        return maintenanceDate;
    }

    public void setMaintenancePerson(String maintenancePerson) 
    {
        this.maintenancePerson = maintenancePerson;
    }

    public String getMaintenancePerson() 
    {
        return maintenancePerson;
    }

    public void setMaintenanceCompany(String maintenanceCompany) 
    {
        this.maintenanceCompany = maintenanceCompany;
    }

    public String getMaintenanceCompany() 
    {
        return maintenanceCompany;
    }

    public void setMaintenanceResult(String maintenanceResult) 
    {
        this.maintenanceResult = maintenanceResult;
    }

    public String getMaintenanceResult() 
    {
        return maintenanceResult;
    }

    public void setProblemDesc(String problemDesc) 
    {
        this.problemDesc = problemDesc;
    }

    public String getProblemDesc() 
    {
        return problemDesc;
    }

    public void setSolution(String solution) 
    {
        this.solution = solution;
    }

    public String getSolution() 
    {
        return solution;
    }

    public void setCost(BigDecimal cost) 
    {
        this.cost = cost;
    }

    public BigDecimal getCost() 
    {
        return cost;
    }

    public void setNextMaintenanceDate(Date nextMaintenanceDate) 
    {
        this.nextMaintenanceDate = nextMaintenanceDate;
    }

    public Date getNextMaintenanceDate() 
    {
        return nextMaintenanceDate;
    }

    public void setAttachments(String attachments) 
    {
        this.attachments = attachments;
    }

    public String getAttachments() 
    {
        return attachments;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("recordId", getRecordId())
            .append("extinguisherId", getExtinguisherId())
            .append("labelCode", getLabelCode())
            .append("maintenanceType", getMaintenanceType())
            .append("maintenanceDate", getMaintenanceDate())
            .append("maintenancePerson", getMaintenancePerson())
            .append("maintenanceCompany", getMaintenanceCompany())
            .append("maintenanceResult", getMaintenanceResult())
            .append("problemDesc", getProblemDesc())
            .append("solution", getSolution())
            .append("cost", getCost())
            .append("nextMaintenanceDate", getNextMaintenanceDate())
            .append("attachments", getAttachments())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
