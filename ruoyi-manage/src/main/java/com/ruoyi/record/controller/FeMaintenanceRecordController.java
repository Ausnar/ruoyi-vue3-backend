package com.ruoyi.record.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.record.domain.FeMaintenanceRecord;
import com.ruoyi.record.service.IFeMaintenanceRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 灭火器维护记录Controller
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
@RestController
@RequestMapping("/record/Inspection_Records")
public class FeMaintenanceRecordController extends BaseController
{
    @Autowired
    private IFeMaintenanceRecordService feMaintenanceRecordService;

    /**
     * 查询灭火器维护记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeMaintenanceRecord feMaintenanceRecord)
    {
        startPage();
        List<FeMaintenanceRecord> list = feMaintenanceRecordService.selectFeMaintenanceRecordList(feMaintenanceRecord);
        return getDataTable(list);
    }

    /**
     * 导出灭火器维护记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:export')")
    @Log(title = "灭火器维护记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeMaintenanceRecord feMaintenanceRecord)
    {
        List<FeMaintenanceRecord> list = feMaintenanceRecordService.selectFeMaintenanceRecordList(feMaintenanceRecord);
        ExcelUtil<FeMaintenanceRecord> util = new ExcelUtil<FeMaintenanceRecord>(FeMaintenanceRecord.class);
        util.exportExcel(response, list, "灭火器维护记录数据");
    }

    /**
     * 获取灭火器维护记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:query')")
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(feMaintenanceRecordService.selectFeMaintenanceRecordByRecordId(recordId));
    }

    /**
     * 新增灭火器维护记录
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:add')")
    @Log(title = "灭火器维护记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeMaintenanceRecord feMaintenanceRecord)
    {
        return toAjax(feMaintenanceRecordService.insertFeMaintenanceRecord(feMaintenanceRecord));
    }

    /**
     * 修改灭火器维护记录
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:edit')")
    @Log(title = "灭火器维护记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeMaintenanceRecord feMaintenanceRecord)
    {
        return toAjax(feMaintenanceRecordService.updateFeMaintenanceRecord(feMaintenanceRecord));
    }

    /**
     * 删除灭火器维护记录
     */
    @PreAuthorize("@ss.hasPermi('record:Inspection_Records:remove')")
    @Log(title = "灭火器维护记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(feMaintenanceRecordService.deleteFeMaintenanceRecordByRecordIds(recordIds));
    }
}
