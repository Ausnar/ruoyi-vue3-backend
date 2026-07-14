package com.ruoyi.manage.domain.report;

import java.io.Serializable;

public class RuntimeDetailFirePointOption implements Serializable
{
    private static final long serialVersionUID = 1L;

    private Long firePointId;
    private String firePointName;
    private String deptName;

    public Long getFirePointId() { return firePointId; }
    public void setFirePointId(Long firePointId) { this.firePointId = firePointId; }
    public String getFirePointName() { return firePointName; }
    public void setFirePointName(String firePointName) { this.firePointName = firePointName; }
    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }
}
