package com.ruoyi.issue.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.issue.domain.FeIssueProjectModule;
import com.ruoyi.issue.service.IFeIssueProjectModuleService;

@RestController
@RequestMapping("/issue/project-module")
public class FeIssueProjectModuleController extends BaseController
{
    @Autowired
    private IFeIssueProjectModuleService issueProjectModuleService;

    @PreAuthorize("@ss.hasPermi('issue:module:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeIssueProjectModule module)
    {
        startPage();
        List<FeIssueProjectModule> list = issueProjectModuleService.selectFeIssueProjectModuleList(module);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('issue:module:query')")
    @GetMapping("/{moduleId}")
    public AjaxResult getInfo(@PathVariable("moduleId") Long moduleId)
    {
        return success(issueProjectModuleService.selectFeIssueProjectModuleByModuleId(moduleId));
    }

    @PreAuthorize("@ss.hasPermi('issue:module:query')")
    @GetMapping("/options/active")
    public AjaxResult activeOptions(@RequestParam("projectId") Long projectId)
    {
        return success(issueProjectModuleService.selectActiveModuleOptions(projectId));
    }

    @PreAuthorize("@ss.hasPermi('issue:module:add')")
    @Log(title = "项目模块", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeIssueProjectModule module)
    {
        return toAjax(issueProjectModuleService.insertFeIssueProjectModule(module));
    }

    @PreAuthorize("@ss.hasPermi('issue:module:edit')")
    @Log(title = "项目模块", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeIssueProjectModule module)
    {
        return toAjax(issueProjectModuleService.updateFeIssueProjectModule(module));
    }

    @PreAuthorize("@ss.hasPermi('issue:module:edit')")
    @Log(title = "项目模块", businessType = BusinessType.UPDATE)
    @PostMapping("/{moduleId}/toggle")
    public AjaxResult toggle(@PathVariable("moduleId") Long moduleId)
    {
        return toAjax(issueProjectModuleService.toggleFeIssueProjectModule(moduleId));
    }

    @PreAuthorize("@ss.hasPermi('issue:module:export')")
    @Log(title = "项目模块", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeIssueProjectModule module)
    {
        List<FeIssueProjectModule> list = issueProjectModuleService.selectFeIssueProjectModuleList(module);
        ExcelUtil<FeIssueProjectModule> util = new ExcelUtil<FeIssueProjectModule>(FeIssueProjectModule.class);
        util.exportExcel(response, list, "项目模块数据");
    }
}
