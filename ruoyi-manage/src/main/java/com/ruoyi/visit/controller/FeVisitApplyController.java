package com.ruoyi.visit.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.visit.domain.FeVisitApply;
import com.ruoyi.visit.domain.FeVisitApplyLog;
import com.ruoyi.visit.domain.dto.VisitAuditRequest;
import com.ruoyi.visit.domain.dto.VisitFeedbackRequest;
import com.ruoyi.visit.service.IFeVisitApplyService;

@RestController
@RequestMapping("/visit/apply")
public class FeVisitApplyController extends BaseController
{
    @Autowired
    private IFeVisitApplyService feVisitApplyService;

    @PreAuthorize("@ss.hasPermi('visit:apply:list')")
    @GetMapping("/my-list")
    public TableDataInfo myList(FeVisitApply apply)
    {
        startPage();
        List<FeVisitApply> list = feVisitApplyService.selectMyVisitApplyList(apply);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:query')")
    @GetMapping("/approve-list")
    public TableDataInfo approveList(FeVisitApply apply)
    {
        startPage();
        List<FeVisitApply> list = feVisitApplyService.selectApproveVisitApplyList(apply);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:query')")
    @GetMapping("/{visitId}")
    public AjaxResult getInfo(@PathVariable Long visitId)
    {
        return success(feVisitApplyService.selectFeVisitApplyByVisitId(visitId));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:query')")
    @GetMapping("/{visitId}/logs")
    public AjaxResult logs(@PathVariable Long visitId)
    {
        List<FeVisitApplyLog> logs = feVisitApplyService.selectFeVisitApplyLogListByVisitId(visitId);
        return success(logs);
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:add')")
    @Log(title = "外出拜访申请", businessType = BusinessType.INSERT)
    @PostMapping("/submit")
    public AjaxResult submit(@RequestBody FeVisitApply apply)
    {
        return toAjax(feVisitApplyService.submitVisitApply(apply));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:resubmit')")
    @Log(title = "外出拜访申请", businessType = BusinessType.UPDATE)
    @PutMapping("/resubmit")
    public AjaxResult resubmit(@RequestBody FeVisitApply apply)
    {
        return toAjax(feVisitApplyService.resubmitVisitApply(apply));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:withdraw')")
    @Log(title = "外出拜访申请", businessType = BusinessType.UPDATE)
    @PostMapping("/{visitId}/withdraw")
    public AjaxResult withdraw(@PathVariable Long visitId)
    {
        return toAjax(feVisitApplyService.withdrawVisitApply(visitId));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:approve')")
    @Log(title = "外出拜访审批", businessType = BusinessType.UPDATE)
    @PostMapping("/{visitId}/approve")
    public AjaxResult approve(@PathVariable Long visitId, @RequestBody(required = false) VisitAuditRequest request)
    {
        return toAjax(feVisitApplyService.approveVisitApply(visitId, request));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:reject')")
    @Log(title = "外出拜访审批", businessType = BusinessType.UPDATE)
    @PostMapping("/{visitId}/reject")
    public AjaxResult reject(@PathVariable Long visitId, @RequestBody(required = false) VisitAuditRequest request)
    {
        return toAjax(feVisitApplyService.rejectVisitApply(visitId, request));
    }

    @PreAuthorize("@ss.hasPermi('visit:apply:feedback')")
    @Log(title = "外出拜访结果回填", businessType = BusinessType.UPDATE)
    @PostMapping("/feedback")
    public AjaxResult feedback(@RequestBody VisitFeedbackRequest request)
    {
        return toAjax(feVisitApplyService.feedbackVisitApply(request));
    }
}
