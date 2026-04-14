package com.ruoyi.visit.domain;

import java.util.List;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeVisitCustomer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long customerId;
    private String customerName;
    private String contactPerson;
    private String contactPhone;
    private String address;
    private Long deptId;
    private String deptName;
    private String status;
    private String delFlag;
    private List<Long> scopeDeptIds;

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public void setCustomerName(String customerName)
    {
        this.customerName = customerName;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone()
    {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone)
    {
        this.contactPhone = contactPhone;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public Long getDeptId()
    {
        return deptId;
    }

    public void setDeptId(Long deptId)
    {
        this.deptId = deptId;
    }

    public String getDeptName()
    {
        return deptName;
    }

    public void setDeptName(String deptName)
    {
        this.deptName = deptName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public List<Long> getScopeDeptIds()
    {
        return scopeDeptIds;
    }

    public void setScopeDeptIds(List<Long> scopeDeptIds)
    {
        this.scopeDeptIds = scopeDeptIds;
    }
}
