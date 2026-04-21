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
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.issue.domain.FeIssueProject;
import com.ruoyi.issue.service.IFeIssueProjectService;

@RestController
@RequestMapping("/issue/project")
public class FeIssueProjectController extends BaseController
{
    @Autowired
    private IFeIssueProjectService issueProjectService;

    @PreAuthorize("@ss.hasPermi('issue:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeIssueProject project)
    {
        startPage();
        List<FeIssueProject> list = issueProjectService.selectFeIssueProjectList(project);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('issue:project:query')")
    @GetMapping("/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId)
    {
        return success(issueProjectService.selectFeIssueProjectByProjectId(projectId));
    }

    @PreAuthorize("@ss.hasPermi('issue:project:query')")
    @GetMapping("/options/active")
    public AjaxResult activeOptions()
    {
        return success(issueProjectService.selectActiveProjectOptions());
    }

    @PreAuthorize("@ss.hasPermi('issue:project:add')")
    @Log(title = "问题项目", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeIssueProject project)
    {
        return toAjax(issueProjectService.insertFeIssueProject(project));
    }

    @PreAuthorize("@ss.hasPermi('issue:project:edit')")
    @Log(title = "问题项目", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeIssueProject project)
    {
        return toAjax(issueProjectService.updateFeIssueProject(project));
    }

    @PreAuthorize("@ss.hasPermi('issue:project:edit')")
    @Log(title = "问题项目", businessType = BusinessType.UPDATE)
    @PostMapping("/{projectId}/toggle")
    public AjaxResult toggle(@PathVariable("projectId") Long projectId)
    {
        return toAjax(issueProjectService.toggleFeIssueProject(projectId));
    }

    @PreAuthorize("@ss.hasPermi('issue:project:export')")
    @Log(title = "问题项目", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeIssueProject project)
    {
        List<FeIssueProject> list = issueProjectService.selectFeIssueProjectList(project);
        ExcelUtil<FeIssueProject> util = new ExcelUtil<FeIssueProject>(FeIssueProject.class);
        util.exportExcel(response, list, "问题项目数据");
    }
}
