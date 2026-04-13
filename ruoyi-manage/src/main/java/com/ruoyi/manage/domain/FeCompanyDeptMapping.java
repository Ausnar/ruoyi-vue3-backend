package com.ruoyi.manage.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeCompanyDeptMapping extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @Excel(name = "映射ID")
    private Long mappingId;
    @Excel(name = "外部公司ID")
    private Long externalCompanyId;
    @Excel(name = "外部单位")
    private String externalCompanyName;
    @Excel(name = "归属单位ID")
    private Long deptId;
    @Excel(name = "归属单位")
    private String deptName;
    @Excel(name = "状态")
    private String syncStatus;

    public Long getMappingId() { return mappingId; }
    public void setMappingId(Long mappingId) { this.mappingId = mappingId; }
    public Long getExternalCompanyId() { return externalCompanyId; }
    public void setExternalCompanyId(Long externalCompanyId) { this.externalCompanyId = externalCompanyId; }
    public String getExternalCompanyName() { return externalCompanyName; }
    public void setExternalCompanyName(String externalCompanyName) { this.externalCompanyName = externalCompanyName; }
    public Long getDeptId() { return deptId; }
    public void setDeptId(Long deptId) { this.deptId = deptId; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
    public String getSyncStatus() { return syncStatus; }
    public void setSyncStatus(String syncStatus) { this.syncStatus = syncStatus; }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("mappingId", mappingId)
            .append("externalCompanyId", externalCompanyId)
            .append("externalCompanyName", externalCompanyName)
            .append("deptId", deptId)
            .append("syncStatus", syncStatus)
            .append("remark", getRemark())
            .toString();
    }
}
