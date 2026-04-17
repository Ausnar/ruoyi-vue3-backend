package com.ruoyi.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.dto.FeExternalCompanyMergeRequest;
import com.ruoyi.manage.service.IFeExternalCompanyService;

@RestController
@RequestMapping("/manage/externalCompany")
public class FeExternalCompanyController extends BaseController
{
    @Autowired
    private IFeExternalCompanyService externalCompanyService;

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeExternalCompany company)
    {
        startPage();
        List<FeExternalCompany> list = externalCompanyService.selectFeExternalCompanyList(company);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:query')")
    @GetMapping("/{companyRecordId}")
    public AjaxResult getInfo(@PathVariable("companyRecordId") Long companyRecordId)
    {
        return success(externalCompanyService.selectFeExternalCompanyByCompanyRecordId(companyRecordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:add')")
    @Log(title = "外部单位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeExternalCompany company)
    {
        return toAjax(externalCompanyService.insertFeExternalCompany(company));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:edit')")
    @Log(title = "外部单位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeExternalCompany company)
    {
        return toAjax(externalCompanyService.updateFeExternalCompany(company));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:edit')")
    @Log(title = "外部单位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{companyRecordId}/toggle")
    public AjaxResult toggle(@PathVariable("companyRecordId") Long companyRecordId)
    {
        return toAjax(externalCompanyService.toggleFeExternalCompany(companyRecordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:query')")
    @GetMapping("/{companyRecordId}/duplicates")
    public AjaxResult duplicates(@PathVariable("companyRecordId") Long companyRecordId)
    {
        return success(externalCompanyService.selectDuplicateCandidates(companyRecordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:merge')")
    @Log(title = "外部单位管理", businessType = BusinessType.UPDATE)
    @PostMapping("/merge")
    public AjaxResult merge(@RequestBody FeExternalCompanyMergeRequest request)
    {
        return toAjax(externalCompanyService.mergeFeExternalCompany(request));
    }

    @PreAuthorize("@ss.hasPermi('manage:externalCompany:export')")
    @Log(title = "外部单位管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeExternalCompany company)
    {
        List<FeExternalCompany> list = externalCompanyService.selectFeExternalCompanyList(company);
        ExcelUtil<FeExternalCompany> util = new ExcelUtil<FeExternalCompany>(FeExternalCompany.class);
        util.exportExcel(response, list, "外部单位数据");
    }
}
