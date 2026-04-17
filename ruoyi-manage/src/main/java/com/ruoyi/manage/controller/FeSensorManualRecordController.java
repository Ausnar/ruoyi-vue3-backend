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
import com.ruoyi.manage.domain.FeSensorManualRecord;
import com.ruoyi.manage.service.IFeSensorManualRecordService;

@RestController
@RequestMapping("/manage/manual-sensor")
public class FeSensorManualRecordController extends BaseController
{
    @Autowired
    private IFeSensorManualRecordService sensorManualRecordService;

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeSensorManualRecord record)
    {
        startPage();
        List<FeSensorManualRecord> list = sensorManualRecordService.selectFeSensorManualRecordList(record);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:export')")
    @Log(title = "人工施工台账-传感器记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeSensorManualRecord record)
    {
        List<FeSensorManualRecord> list = sensorManualRecordService.selectFeSensorManualRecordList(record);
        ExcelUtil<FeSensorManualRecord> util = new ExcelUtil<FeSensorManualRecord>(FeSensorManualRecord.class);
        util.exportExcel(response, list, "传感器施工记录");
    }

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:query')")
    @GetMapping("/{sensorRecordId}")
    public AjaxResult getInfo(@PathVariable("sensorRecordId") Long sensorRecordId)
    {
        return success(sensorManualRecordService.selectFeSensorManualRecordBySensorRecordId(sensorRecordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:add')")
    @Log(title = "人工施工台账-传感器记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeSensorManualRecord record)
    {
        return toAjax(sensorManualRecordService.insertFeSensorManualRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:edit')")
    @Log(title = "人工施工台账-传感器记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeSensorManualRecord record)
    {
        return toAjax(sensorManualRecordService.updateFeSensorManualRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualSensor:void')")
    @Log(title = "人工施工台账-传感器记录", businessType = BusinessType.UPDATE)
    @PostMapping("/{sensorRecordId}/void")
    public AjaxResult voidRecord(@PathVariable("sensorRecordId") Long sensorRecordId)
    {
        return toAjax(sensorManualRecordService.voidFeSensorManualRecord(sensorRecordId));
    }
}
