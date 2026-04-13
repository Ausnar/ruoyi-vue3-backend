package com.ruoyi.manage.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.service.IFeCompanyDeptMappingService;

@RestController
@RequestMapping("/manage/companyDeptMapping")
public class FeCompanyDeptMappingController extends BaseController
{
    @Autowired
    private IFeCompanyDeptMappingService feCompanyDeptMappingService;

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeCompanyDeptMapping mapping)
    {
        startPage();
        List<FeCompanyDeptMapping> list = feCompanyDeptMappingService.selectFeCompanyDeptMappingList(mapping);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:query')")
    @GetMapping("/{mappingId}")
    public AjaxResult getInfo(@PathVariable("mappingId") Long mappingId)
    {
        return success(feCompanyDeptMappingService.selectFeCompanyDeptMappingByMappingId(mappingId));
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:add')")
    @Log(title = "外部公司映射", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeCompanyDeptMapping mapping)
    {
        return toAjax(feCompanyDeptMappingService.insertFeCompanyDeptMapping(mapping));
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:edit')")
    @Log(title = "外部公司映射", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeCompanyDeptMapping mapping)
    {
        return toAjax(feCompanyDeptMappingService.updateFeCompanyDeptMapping(mapping));
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:remove')")
    @Log(title = "外部公司映射", businessType = BusinessType.DELETE)
    @DeleteMapping("/{mappingIds}")
    public AjaxResult remove(@PathVariable Long[] mappingIds)
    {
        return toAjax(feCompanyDeptMappingService.deleteFeCompanyDeptMappingByMappingIds(mappingIds));
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:list')")
    @GetMapping("/externalCompanies")
    public AjaxResult externalCompanies(FeExternalCompany company)
    {
        return success(feCompanyDeptMappingService.selectExternalCompanyOptions(company));
    }

    @PreAuthorize("@ss.hasPermi('manage:companyDeptMapping:export')")
    @Log(title = "外部公司映射", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeCompanyDeptMapping mapping)
    {
        List<FeCompanyDeptMapping> list = feCompanyDeptMappingService.selectFeCompanyDeptMappingList(mapping);
        ExcelUtil<FeCompanyDeptMapping> util = new ExcelUtil<FeCompanyDeptMapping>(FeCompanyDeptMapping.class);
        util.exportExcel(response, list, "外部公司映射数据");
    }
}
