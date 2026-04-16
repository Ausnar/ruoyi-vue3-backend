package com.ruoyi.visit.domain.dto;

public class VisitPassiveEventConfirmRequest
{
    private String selectedTargetType;
    private Long selectedTargetId;
    private Long selectedExternalCompanyId;

    public String getSelectedTargetType()
    {
        return selectedTargetType;
    }

    public void setSelectedTargetType(String selectedTargetType)
    {
        this.selectedTargetType = selectedTargetType;
    }

    public Long getSelectedTargetId()
    {
        return selectedTargetId;
    }

    public void setSelectedTargetId(Long selectedTargetId)
    {
        this.selectedTargetId = selectedTargetId;
    }

    public Long getSelectedExternalCompanyId()
    {
        return selectedExternalCompanyId;
    }

    public void setSelectedExternalCompanyId(Long selectedExternalCompanyId)
    {
        this.selectedExternalCompanyId = selectedExternalCompanyId;
    }
}
