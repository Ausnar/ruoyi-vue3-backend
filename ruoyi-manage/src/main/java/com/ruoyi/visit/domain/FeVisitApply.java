package com.ruoyi.visit.domain;

import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

public class FeVisitApply extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long visitId;
    private String visitNo;
    private Long applicantUserId;
    private String applicantUserName;
    private Long applicantDeptId;
    private String applicantDeptName;
    private String customerType;
    private Long contractDeptId;
    private Long contractConfigId;
    private Long customerId;
    private String customerNameSnapshot;
    private String contactPersonSnapshot;
    private String contactPhoneSnapshot;
    private String visitAddress;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date plannedStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date plannedEndTime;

    private String visitReason;
    private String visitTarget;
    private String companionMembers;
    private String status;
    private Long approveRoleIdSnapshot;
    private Long approveUserId;
    private String approveUserName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    private String approveComment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date actualEndTime;

    private String resultDesc;
    private String visitConclusion;
    private String intentionLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date nextFollowTime;

    private String resultAttachments;
    private String delFlag;
    private Boolean canApprove;
    private String excludeStatus;
    private List<Long> scopeDeptIds;

    public Long getVisitId()
    {
        return visitId;
    }

    public void setVisitId(Long visitId)
    {
        this.visitId = visitId;
    }

    public String getVisitNo()
    {
        return visitNo;
    }

    public void setVisitNo(String visitNo)
    {
        this.visitNo = visitNo;
    }

    public Long getApplicantUserId()
    {
        return applicantUserId;
    }

    public void setApplicantUserId(Long applicantUserId)
    {
        this.applicantUserId = applicantUserId;
    }

    public String getApplicantUserName()
    {
        return applicantUserName;
    }

    public void setApplicantUserName(String applicantUserName)
    {
        this.applicantUserName = applicantUserName;
    }

    public Long getApplicantDeptId()
    {
        return applicantDeptId;
    }

    public void setApplicantDeptId(Long applicantDeptId)
    {
        this.applicantDeptId = applicantDeptId;
    }

    public String getApplicantDeptName()
    {
        return applicantDeptName;
    }

    public void setApplicantDeptName(String applicantDeptName)
    {
        this.applicantDeptName = applicantDeptName;
    }

    public String getCustomerType()
    {
        return customerType;
    }

    public void setCustomerType(String customerType)
    {
        this.customerType = customerType;
    }

    public Long getContractDeptId()
    {
        return contractDeptId;
    }

    public void setContractDeptId(Long contractDeptId)
    {
        this.contractDeptId = contractDeptId;
    }

    public Long getContractConfigId()
    {
        return contractConfigId;
    }

    public void setContractConfigId(Long contractConfigId)
    {
        this.contractConfigId = contractConfigId;
    }

    public Long getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(Long customerId)
    {
        this.customerId = customerId;
    }

    public String getCustomerNameSnapshot()
    {
        return customerNameSnapshot;
    }

    public void setCustomerNameSnapshot(String customerNameSnapshot)
    {
        this.customerNameSnapshot = customerNameSnapshot;
    }

    public String getContactPersonSnapshot()
    {
        return contactPersonSnapshot;
    }

    public void setContactPersonSnapshot(String contactPersonSnapshot)
    {
        this.contactPersonSnapshot = contactPersonSnapshot;
    }

    public String getContactPhoneSnapshot()
    {
        return contactPhoneSnapshot;
    }

    public void setContactPhoneSnapshot(String contactPhoneSnapshot)
    {
        this.contactPhoneSnapshot = contactPhoneSnapshot;
    }

    public String getVisitAddress()
    {
        return visitAddress;
    }

    public void setVisitAddress(String visitAddress)
    {
        this.visitAddress = visitAddress;
    }

    public Date getPlannedStartTime()
    {
        return plannedStartTime;
    }

    public void setPlannedStartTime(Date plannedStartTime)
    {
        this.plannedStartTime = plannedStartTime;
    }

    public Date getPlannedEndTime()
    {
        return plannedEndTime;
    }

    public void setPlannedEndTime(Date plannedEndTime)
    {
        this.plannedEndTime = plannedEndTime;
    }

    public String getVisitReason()
    {
        return visitReason;
    }

    public void setVisitReason(String visitReason)
    {
        this.visitReason = visitReason;
    }

    public String getVisitTarget()
    {
        return visitTarget;
    }

    public void setVisitTarget(String visitTarget)
    {
        this.visitTarget = visitTarget;
    }

    public String getCompanionMembers()
    {
        return companionMembers;
    }

    public void setCompanionMembers(String companionMembers)
    {
        this.companionMembers = companionMembers;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public Long getApproveRoleIdSnapshot()
    {
        return approveRoleIdSnapshot;
    }

    public void setApproveRoleIdSnapshot(Long approveRoleIdSnapshot)
    {
        this.approveRoleIdSnapshot = approveRoleIdSnapshot;
    }

    public Long getApproveUserId()
    {
        return approveUserId;
    }

    public void setApproveUserId(Long approveUserId)
    {
        this.approveUserId = approveUserId;
    }

    public String getApproveUserName()
    {
        return approveUserName;
    }

    public void setApproveUserName(String approveUserName)
    {
        this.approveUserName = approveUserName;
    }

    public Date getApproveTime()
    {
        return approveTime;
    }

    public void setApproveTime(Date approveTime)
    {
        this.approveTime = approveTime;
    }

    public String getApproveComment()
    {
        return approveComment;
    }

    public void setApproveComment(String approveComment)
    {
        this.approveComment = approveComment;
    }

    public Date getActualStartTime()
    {
        return actualStartTime;
    }

    public void setActualStartTime(Date actualStartTime)
    {
        this.actualStartTime = actualStartTime;
    }

    public Date getActualEndTime()
    {
        return actualEndTime;
    }

    public void setActualEndTime(Date actualEndTime)
    {
        this.actualEndTime = actualEndTime;
    }

    public String getResultDesc()
    {
        return resultDesc;
    }

    public void setResultDesc(String resultDesc)
    {
        this.resultDesc = resultDesc;
    }

    public String getVisitConclusion()
    {
        return visitConclusion;
    }

    public void setVisitConclusion(String visitConclusion)
    {
        this.visitConclusion = visitConclusion;
    }

    public String getIntentionLevel()
    {
        return intentionLevel;
    }

    public void setIntentionLevel(String intentionLevel)
    {
        this.intentionLevel = intentionLevel;
    }

    public Date getNextFollowTime()
    {
        return nextFollowTime;
    }

    public void setNextFollowTime(Date nextFollowTime)
    {
        this.nextFollowTime = nextFollowTime;
    }

    public String getResultAttachments()
    {
        return resultAttachments;
    }

    public void setResultAttachments(String resultAttachments)
    {
        this.resultAttachments = resultAttachments;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    public Boolean getCanApprove()
    {
        return canApprove;
    }

    public void setCanApprove(Boolean canApprove)
    {
        this.canApprove = canApprove;
    }

    public String getExcludeStatus()
    {
        return excludeStatus;
    }

    public void setExcludeStatus(String excludeStatus)
    {
        this.excludeStatus = excludeStatus;
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
