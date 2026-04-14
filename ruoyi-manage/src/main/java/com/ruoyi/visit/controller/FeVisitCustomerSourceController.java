package com.ruoyi.visit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.visit.service.IFeVisitApplyService;

@RestController
@RequestMapping("/visit/customer-source")
public class FeVisitCustomerSourceController extends BaseController
{
    @Autowired
    private IFeVisitApplyService feVisitApplyService;

    @PreAuthorize("@ss.hasAnyPermi('visit:apply:add,visit:apply:edit,visit:apply:query')")
    @GetMapping("/contract-options")
    public AjaxResult contractOptions()
    {
        return success(feVisitApplyService.selectContractOptions());
    }
}
