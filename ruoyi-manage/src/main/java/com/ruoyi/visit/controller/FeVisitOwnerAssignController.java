package com.ruoyi.visit.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ruoyi.visit.domain.FeVisitOwnerAssign;
import com.ruoyi.visit.service.IFeVisitOwnerAssignService;

@RestController
@RequestMapping("/visit/owner-assign")
public class FeVisitOwnerAssignController extends BaseController
{
    @Autowired
    private IFeVisitOwnerAssignService feVisitOwnerAssignService;

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:query')")
    @GetMapping("/list")
    public TableDataInfo list(FeVisitOwnerAssign assign)
    {
        startPage();
        List<FeVisitOwnerAssign> list = feVisitOwnerAssignService.selectFeVisitOwnerAssignList(assign);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:query')")
    @GetMapping("/{assignId}")
    public AjaxResult getInfo(@PathVariable Long assignId)
    {
        return success(feVisitOwnerAssignService.selectFeVisitOwnerAssignByAssignId(assignId));
    }

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:query')")
    @GetMapping("/contract-options")
    public AjaxResult contractOptions()
    {
        return success(feVisitOwnerAssignService.selectContractOptions());
    }

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:add')")
    @Log(title = "责任分配", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeVisitOwnerAssign assign)
    {
        return toAjax(feVisitOwnerAssignService.insertFeVisitOwnerAssign(assign));
    }

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:edit')")
    @Log(title = "责任分配", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeVisitOwnerAssign assign)
    {
        return toAjax(feVisitOwnerAssignService.updateFeVisitOwnerAssign(assign));
    }

    @PreAuthorize("@ss.hasPermi('visit:ownerAssign:remove')")
    @Log(title = "责任分配", businessType = BusinessType.DELETE)
    @DeleteMapping("/{assignIds}")
    public AjaxResult remove(@PathVariable Long[] assignIds)
    {
        return toAjax(feVisitOwnerAssignService.deleteFeVisitOwnerAssignByAssignIds(assignIds));
    }
}
