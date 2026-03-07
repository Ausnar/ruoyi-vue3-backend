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
import com.ruoyi.record.domain.FeAlarmRecord;
import com.ruoyi.record.service.IFeAlarmRecordService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 报警记录Controller
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
@RestController
@RequestMapping("/record/Warning_records")
public class FeAlarmRecordController extends BaseController
{
    @Autowired
    private IFeAlarmRecordService feAlarmRecordService;

    /**
     * 查询报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeAlarmRecord feAlarmRecord)
    {
        startPage();
        List<FeAlarmRecord> list = feAlarmRecordService.selectFeAlarmRecordList(feAlarmRecord);
        return getDataTable(list);
    }

    /**
     * 导出报警记录列表
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:export')")
    @Log(title = "报警记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeAlarmRecord feAlarmRecord)
    {
        List<FeAlarmRecord> list = feAlarmRecordService.selectFeAlarmRecordList(feAlarmRecord);
        ExcelUtil<FeAlarmRecord> util = new ExcelUtil<FeAlarmRecord>(FeAlarmRecord.class);
        util.exportExcel(response, list, "报警记录数据");
    }

    /**
     * 获取报警记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:query')")
    @GetMapping(value = "/{alarmId}")
    public AjaxResult getInfo(@PathVariable("alarmId") Long alarmId)
    {
        return success(feAlarmRecordService.selectFeAlarmRecordByAlarmId(alarmId));
    }

    /**
     * 新增报警记录
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:add')")
    @Log(title = "报警记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeAlarmRecord feAlarmRecord)
    {
        return toAjax(feAlarmRecordService.insertFeAlarmRecord(feAlarmRecord));
    }

    /**
     * 修改报警记录
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:edit')")
    @Log(title = "报警记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeAlarmRecord feAlarmRecord)
    {
        return toAjax(feAlarmRecordService.updateFeAlarmRecord(feAlarmRecord));
    }

    /**
     * 删除报警记录
     */
    @PreAuthorize("@ss.hasPermi('record:Warning_records:remove')")
    @Log(title = "报警记录", businessType = BusinessType.DELETE)
	@DeleteMapping("/{alarmIds}")
    public AjaxResult remove(@PathVariable Long[] alarmIds)
    {
        return toAjax(feAlarmRecordService.deleteFeAlarmRecordByAlarmIds(alarmIds));
    }
}
