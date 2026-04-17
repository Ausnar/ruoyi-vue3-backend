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
import com.ruoyi.manage.domain.FeGatewayManualRecord;
import com.ruoyi.manage.service.IFeGatewayManualRecordService;

@RestController
@RequestMapping("/manage/manual-gateway")
public class FeGatewayManualRecordController extends BaseController
{
    @Autowired
    private IFeGatewayManualRecordService gatewayManualRecordService;

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeGatewayManualRecord record)
    {
        startPage();
        List<FeGatewayManualRecord> list = gatewayManualRecordService.selectFeGatewayManualRecordList(record);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:export')")
    @Log(title = "人工施工台账-网关记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeGatewayManualRecord record)
    {
        List<FeGatewayManualRecord> list = gatewayManualRecordService.selectFeGatewayManualRecordList(record);
        ExcelUtil<FeGatewayManualRecord> util = new ExcelUtil<FeGatewayManualRecord>(FeGatewayManualRecord.class);
        util.exportExcel(response, list, "网关施工记录");
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:query')")
    @GetMapping("/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(gatewayManualRecordService.selectFeGatewayManualRecordByRecordId(recordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:add')")
    @Log(title = "人工施工台账-网关记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeGatewayManualRecord record)
    {
        return toAjax(gatewayManualRecordService.insertFeGatewayManualRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:edit')")
    @Log(title = "人工施工台账-网关记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeGatewayManualRecord record)
    {
        return toAjax(gatewayManualRecordService.updateFeGatewayManualRecord(record));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:void')")
    @Log(title = "人工施工台账-网关记录", businessType = BusinessType.UPDATE)
    @PostMapping("/{recordId}/void")
    public AjaxResult voidRecord(@PathVariable("recordId") Long recordId)
    {
        return toAjax(gatewayManualRecordService.voidFeGatewayManualRecord(recordId));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualGateway:list')")
    @GetMapping("/externalCompanies")
    public AjaxResult externalCompanies(FeExternalCompany company)
    {
        return success(gatewayManualRecordService.selectExternalCompanyOptions(company));
    }

    @PreAuthorize("@ss.hasPermi('manage:manualExternalCompany:add')")
    @Log(title = "人工施工台账-外部单位", businessType = BusinessType.INSERT)
    @PostMapping("/externalCompanies")
    public AjaxResult createExternalCompany(@RequestBody FeExternalCompany company)
    {
        return success(gatewayManualRecordService.createExternalCompany(company));
    }
}
