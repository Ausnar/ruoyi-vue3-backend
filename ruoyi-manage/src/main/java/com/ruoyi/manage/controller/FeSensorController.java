package com.ruoyi.manage.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.service.IFeSensorService;
import com.ruoyi.manage.domain.FeSensorHistory;
import com.ruoyi.manage.service.IFeSensorHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 传感器管理Controller
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/manage/sensor")
public class FeSensorController extends BaseController
{
    @Autowired
    private IFeSensorService feSensorService;

    @Autowired
    private IFeSensorHistoryService feSensorHistoryService;

    /**
     * 查询传感器管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeSensor feSensor)
    {
        startPage();
        List<FeSensor> list = feSensorService.selectFeSensorList(feSensor);
        return getDataTable(list);
    }

    /**
     * 导出传感器管理列表
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:export')")
    @Log(title = "传感器管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeSensor feSensor)
    {
        List<FeSensor> list = feSensorService.selectFeSensorList(feSensor);
        ExcelUtil<FeSensor> util = new ExcelUtil<FeSensor>(FeSensor.class);
        util.exportExcel(response, list, "传感器管理数据");
    }

    /**
     * 获取传感器管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:query')")
    @GetMapping(value = "/{sensorId}")
    public AjaxResult getInfo(@PathVariable("sensorId") Long sensorId)
    {
        return success(feSensorService.selectFeSensorBySensorId(sensorId));
    }

    /**
     * 新增传感器管理
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:add')")
    @Log(title = "传感器管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeSensor feSensor)
    {
        return toAjax(feSensorService.insertFeSensor(feSensor));
    }

    /**
     * 修改传感器管理
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:edit')")
    @Log(title = "传感器管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeSensor feSensor)
    {
        return toAjax(feSensorService.updateFeSensor(feSensor));
    }

    /**
     * 删除传感器管理
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:remove')")
    @Log(title = "传感器管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{sensorIds}")
    public AjaxResult remove(@PathVariable Long[] sensorIds)
    {
        return toAjax(feSensorService.deleteFeSensorBySensorIds(sensorIds));
    }

    /**
     * 查询传感器历史数据列表
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:query')")
    @GetMapping("/history/list")
    public TableDataInfo listHistory(FeSensorHistory feSensorHistory)
    {
        startPage();
        List<FeSensorHistory> list = feSensorHistoryService.selectFeSensorHistoryList(feSensorHistory);
        return getDataTable(list);
    }

    /**
     * 获取传感器历史数据（用于图表）
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:query')")
    @GetMapping("/history/chart")
    public AjaxResult getChartData(@RequestParam("sensorId") Long sensorId,
                                   @RequestParam(value = "startTime", required = false) String startTime,
                                   @RequestParam(value = "endTime", required = false) String endTime)
    {
        List<FeSensorHistory> list = feSensorHistoryService.selectFeSensorHistoryBySensorId(sensorId, startTime, endTime);
        return success(list);
    }

    /**
     * 导出传感器历史数据
     */
    @PreAuthorize("@ss.hasPermi('manage:sensor:export')")
    @Log(title = "传感器历史数据", businessType = BusinessType.EXPORT)
    @PostMapping("/history/export")
    public void exportHistory(HttpServletResponse response, FeSensorHistory feSensorHistory)
    {
        List<FeSensorHistory> list = feSensorHistoryService.selectFeSensorHistoryList(feSensorHistory);
        ExcelUtil<FeSensorHistory> util = new ExcelUtil<FeSensorHistory>(FeSensorHistory.class);
        util.exportExcel(response, list, "传感器历史数据");
    }
}
