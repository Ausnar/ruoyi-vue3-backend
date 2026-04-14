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
import com.ruoyi.visit.domain.FeVisitCustomer;
import com.ruoyi.visit.service.IFeVisitCustomerService;

@RestController
@RequestMapping("/visit/customer")
public class FeVisitCustomerController extends BaseController
{
    @Autowired
    private IFeVisitCustomerService feVisitCustomerService;

    @PreAuthorize("@ss.hasPermi('visit:customer:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeVisitCustomer customer)
    {
        startPage();
        List<FeVisitCustomer> list = feVisitCustomerService.selectFeVisitCustomerList(customer);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:customer:query')")
    @GetMapping("/{customerId}")
    public AjaxResult getInfo(@PathVariable Long customerId)
    {
        return success(feVisitCustomerService.selectFeVisitCustomerByCustomerId(customerId));
    }

    @PreAuthorize("@ss.hasPermi('visit:customer:add')")
    @Log(title = "独立客户档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeVisitCustomer customer)
    {
        return toAjax(feVisitCustomerService.insertFeVisitCustomer(customer));
    }

    @PreAuthorize("@ss.hasPermi('visit:customer:edit')")
    @Log(title = "独立客户档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeVisitCustomer customer)
    {
        return toAjax(feVisitCustomerService.updateFeVisitCustomer(customer));
    }

    @PreAuthorize("@ss.hasPermi('visit:customer:remove')")
    @Log(title = "独立客户档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{customerIds}")
    public AjaxResult remove(@PathVariable Long[] customerIds)
    {
        return toAjax(feVisitCustomerService.deleteFeVisitCustomerByCustomerIds(customerIds));
    }
}
