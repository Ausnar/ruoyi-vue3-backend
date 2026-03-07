package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 部门API配置对象 sys_dept_api_config
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
public class SysDeptApiConfig extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    private Long configId;

    /** 部门ID */
    @Excel(name = "部门ID")
    private Long deptId;

    /** 合同号 */
    @Excel(name = "合同号")
    private String contractNo;

    /** API ID */
    @Excel(name = "API ID")
    private String apiId;

    /** API KEY */
    @Excel(name = "API KEY")
    private String apiKey;

    /** 状态(0停用 1正常) */
    @Excel(name = "状态", readConverterExp = "0=停用,1=正常")
    private String status;

    /** 到期日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到期日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expireDate;

    // ========== 以下为非数据库字段，用于接收关联查询结果 ==========
    
    /** 部门名称 */
    @Excel(name = "部门名称")
    private String deptName;

    /** 部门负责人 */
    private String deptLeader;

    /** 部门电话 */
    private String deptPhone;

    /** 过期状态 */
    @Excel(name = "过期状态")
    private String expireStatus;

    /** 剩余天数 */
    @Excel(name = "剩余天数")
    private Integer daysRemaining;

    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setContractNo(String contractNo) 
    {
        this.contractNo = contractNo;
    }

    public String getContractNo() 
    {
        return contractNo;
    }

    public void setApiId(String apiId) 
    {
        this.apiId = apiId;
    }

    public String getApiId() 
    {
        return apiId;
    }

    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setExpireDate(Date expireDate) 
    {
        this.expireDate = expireDate;
    }

    public Date getExpireDate() 
    {
        return expireDate;
    }

    public void setDeptName(String deptName) 
    {
        this.deptName = deptName;
    }

    public String getDeptName() 
    {
        return deptName;
    }

    public void setDeptLeader(String deptLeader) 
    {
        this.deptLeader = deptLeader;
    }

    public String getDeptLeader() 
    {
        return deptLeader;
    }

    public void setDeptPhone(String deptPhone) 
    {
        this.deptPhone = deptPhone;
    }

    public String getDeptPhone() 
    {
        return deptPhone;
    }

    public void setExpireStatus(String expireStatus) 
    {
        this.expireStatus = expireStatus;
    }

    public String getExpireStatus() 
    {
        return expireStatus;
    }

    public void setDaysRemaining(Integer daysRemaining) 
    {
        this.daysRemaining = daysRemaining;
    }

    public Integer getDaysRemaining() 
    {
        return daysRemaining;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("configId", getConfigId())
            .append("deptId", getDeptId())
            .append("deptName", getDeptName())
            .append("contractNo", getContractNo())
            .append("apiId", getApiId())
            .append("apiKey", getApiKey())
            .append("status", getStatus())
            .append("expireDate", getExpireDate())
            .append("expireStatus", getExpireStatus())
            .append("daysRemaining", getDaysRemaining())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}