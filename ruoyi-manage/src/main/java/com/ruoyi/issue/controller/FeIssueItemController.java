package com.ruoyi.issue.controller;

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
import com.ruoyi.issue.domain.FeIssueItem;
import com.ruoyi.issue.service.IFeIssueItemService;

@RestController
@RequestMapping("/issue/item")
public class FeIssueItemController extends BaseController
{
    @Autowired
    private IFeIssueItemService issueItemService;

    @PreAuthorize("@ss.hasPermi('issue:item:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeIssueItem item)
    {
        startPage();
        List<FeIssueItem> list = issueItemService.selectFeIssueItemList(item);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('issue:item:query')")
    @GetMapping("/{issueId}")
    public AjaxResult getInfo(@PathVariable("issueId") Long issueId)
    {
        return success(issueItemService.selectFeIssueItemByIssueId(issueId));
    }

    @PreAuthorize("@ss.hasPermi('issue:item:viewLog')")
    @GetMapping("/{issueId}/logs")
    public AjaxResult logs(@PathVariable("issueId") Long issueId)
    {
        return success(issueItemService.selectIssueLogs(issueId));
    }

    @PreAuthorize("@ss.hasPermi('issue:item:add')")
    @Log(title = "问题事项", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeIssueItem item)
    {
        return toAjax(issueItemService.insertFeIssueItem(item));
    }

    @PreAuthorize("@ss.hasPermi('issue:item:edit')")
    @Log(title = "问题事项", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeIssueItem item)
    {
        return toAjax(issueItemService.updateFeIssueItem(item));
    }

    @PreAuthorize("@ss.hasPermi('issue:item:remove')")
    @Log(title = "问题事项", businessType = BusinessType.DELETE)
    @DeleteMapping("/{issueIds}")
    public AjaxResult remove(@PathVariable Long[] issueIds)
    {
        return toAjax(issueItemService.deleteFeIssueItemByIssueIds(issueIds));
    }

    @PreAuthorize("@ss.hasPermi('issue:item:export')")
    @Log(title = "问题事项", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeIssueItem item)
    {
        List<FeIssueItem> list = issueItemService.selectFeIssueItemList(item);
        ExcelUtil<FeIssueItem> util = new ExcelUtil<FeIssueItem>(FeIssueItem.class);
        util.exportExcel(response, list, "问题事项数据");
    }
}
