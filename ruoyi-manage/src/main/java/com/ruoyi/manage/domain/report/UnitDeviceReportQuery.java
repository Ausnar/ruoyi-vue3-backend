package com.ruoyi.manage.domain.report;

import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 单位设备报告查询条件。
 */
public class UnitDeviceReportQuery extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long deptId;
    private String scopeName;

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getScopeName()
    {
        return scopeName;
    }

    public void setScopeName(String scopeName)
    {
        this.scopeName = scopeName;
    }
}
