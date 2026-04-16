package com.ruoyi.visit.service;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitApply;
import com.ruoyi.visit.domain.FeVisitApplyLog;
import com.ruoyi.visit.domain.FeVisitContractOption;
import com.ruoyi.visit.domain.dto.VisitAuditRequest;
import com.ruoyi.visit.domain.dto.VisitFeedbackRequest;

public interface IFeVisitApplyService
{
    List<FeVisitApply> selectMyVisitApplyList(FeVisitApply apply);

    List<FeVisitApply> selectApproveVisitApplyList(FeVisitApply apply);

    FeVisitApply selectFeVisitApplyByVisitId(Long visitId);

    List<FeVisitApplyLog> selectFeVisitApplyLogListByVisitId(Long visitId);

    List<FeVisitContractOption> selectContractOptions();

    List<FeVisitContractOption> selectContractOptionsByDeptIds(List<Long> deptIds);

    int submitVisitApply(FeVisitApply apply);

    int resubmitVisitApply(FeVisitApply apply);

    int withdrawVisitApply(Long visitId);

    int approveVisitApply(Long visitId, VisitAuditRequest request);

    int rejectVisitApply(Long visitId, VisitAuditRequest request);

    int feedbackVisitApply(VisitFeedbackRequest request);

    int createPassiveVisitApply(FeVisitApply apply, Long operatorUserId, Long operatorDeptId, String operatorName);
}
