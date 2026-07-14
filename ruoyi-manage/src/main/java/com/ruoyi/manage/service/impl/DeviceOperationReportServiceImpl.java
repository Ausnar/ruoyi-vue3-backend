package com.ruoyi.manage.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.report.DeviceOperationReportPreview;
import com.ruoyi.manage.domain.report.DeviceOperationReportQuery;
import com.ruoyi.manage.mapper.DeviceOperationReportMapper;
import com.ruoyi.manage.report.DeviceOperationReportExcelExporter;
import com.ruoyi.manage.report.DeviceOperationReportPdfExporter;
import com.ruoyi.manage.service.IDeviceOperationReportService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class DeviceOperationReportServiceImpl implements IDeviceOperationReportService
{
    private static final ZoneId REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private DeviceOperationReportMapper deviceOperationReportMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private DeviceOperationReportExcelExporter excelExporter;

    @Autowired
    private DeviceOperationReportPdfExporter pdfExporter;

    @Override
    @DataScope(deptAlias = "d", permission = "report:deviceOperation:list")
    public DeviceOperationReportPreview preview(DeviceOperationReportQuery query)
    {
        prepareQuery(query);
        return buildPreview(query);
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:deviceOperation:export")
    public void exportExcel(HttpServletResponse response, DeviceOperationReportQuery query)
    {
        prepareQuery(query);
        excelExporter.export(response, buildPreview(query));
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:deviceOperation:export")
    public void exportPdf(HttpServletResponse response, DeviceOperationReportQuery query)
    {
        prepareQuery(query);
        pdfExporter.export(response, buildPreview(query));
    }

    private DeviceOperationReportPreview buildPreview(DeviceOperationReportQuery query)
    {
        DeviceOperationReportPreview preview = new DeviceOperationReportPreview();
        preview.setReportTitle(query.getScopeName().replace("及下级单位", "") + "设备运行" + query.getPeriodTypeName());
        preview.setPeriodTypeName(query.getPeriodTypeName());
        preview.setPeriodLabel(query.getPeriodLabel());
        preview.setScopeName(query.getScopeName());
        preview.setGeneratedTime(DATE_TIME.format(java.time.LocalDateTime.now(REPORT_ZONE)));
        preview.setUnitInfo(orDefault(deviceOperationReportMapper.selectUnitInfo(query), new DeviceOperationReportPreview.UnitInfo()));
        preview.setAssetOverview(orDefault(deviceOperationReportMapper.selectAssetOverview(query), new DeviceOperationReportPreview.AssetOverview()));
        preview.setSensorSummary(orDefault(deviceOperationReportMapper.selectSensorSummary(query), new DeviceOperationReportPreview.SensorSummary()));
        preview.setGatewaySummary(orDefault(deviceOperationReportMapper.selectGatewaySummary(query), new DeviceOperationReportPreview.GatewaySummary()));
        preview.setExtinguisherSummary(orDefault(deviceOperationReportMapper.selectExtinguisherSummary(query), new DeviceOperationReportPreview.ExtinguisherSummary()));
        preview.setFirePointSummary(orDefault(deviceOperationReportMapper.selectFirePointSummary(query), new DeviceOperationReportPreview.FirePointSummary()));
        return preview;
    }

    private <T> T orDefault(T value, T defaultValue)
    {
        return value == null ? defaultValue : value;
    }

    private void prepareQuery(DeviceOperationReportQuery query)
    {
        if (query == null) throw new ServiceException("设备运行报告查询参数不能为空");
        normalizePeriod(query);
        normalizeDept(query);
    }

    private void normalizePeriod(DeviceOperationReportQuery query)
    {
        String periodType = StringUtils.isBlank(query.getPeriodType()) ? "day" : query.getPeriodType().trim();
        if (!"day".equals(periodType) && !"week".equals(periodType) && !"month".equals(periodType))
        {
            throw new ServiceException("报告周期仅支持日报、周报、月报");
        }

        LocalDate anchor;
        try
        {
            anchor = StringUtils.isBlank(query.getPeriodDate()) ? LocalDate.now(REPORT_ZONE) : LocalDate.parse(query.getPeriodDate(), DATE);
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("统计日期格式应为yyyy-MM-dd");
        }

        LocalDate start = periodStart(periodType, anchor);
        LocalDate currentStart = periodStart(periodType, LocalDate.now(REPORT_ZONE));
        if (start.isAfter(currentStart)) throw new ServiceException("不能生成未来统计周期的设备运行报告");
        LocalDate end = periodEnd(periodType, start);
        query.setPeriodType(periodType);
        query.setPeriodDate(start.format(DATE));
        query.setBeginTime(Date.from(start.atStartOfDay(REPORT_ZONE).toInstant()));
        query.setEndTime(Date.from(end.atStartOfDay(REPORT_ZONE).toInstant()));
        query.setPeriodTypeName("day".equals(periodType) ? "日报" : "week".equals(periodType) ? "周报" : "月报");
        query.setPeriodLabel(periodLabel(periodType, start, end));
    }

    private LocalDate periodStart(String periodType, LocalDate anchor)
    {
        if ("week".equals(periodType)) return anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        if ("month".equals(periodType)) return anchor.withDayOfMonth(1);
        return anchor;
    }

    private LocalDate periodEnd(String periodType, LocalDate start)
    {
        if ("week".equals(periodType)) return start.plusWeeks(1);
        if ("month".equals(periodType)) return start.plusMonths(1);
        return start.plusDays(1);
    }

    private String periodLabel(String periodType, LocalDate start, LocalDate end)
    {
        if ("month".equals(periodType)) return start.format(DateTimeFormatter.ofPattern("yyyy年MM月"));
        if ("week".equals(periodType)) return start.format(DISPLAY_DATE) + " 至 " + end.minusDays(1).format(DISPLAY_DATE);
        return start.format(DISPLAY_DATE);
    }

    private void normalizeDept(DeviceOperationReportQuery query)
    {
        if (query.getDeptId() == null) throw new ServiceException("请选择所属单位后再生成设备运行报告");
        if (!SecurityUtils.isAdmin()) sysDeptService.checkDeptDataScope(query.getDeptId());
        SysDept dept = sysDeptService.selectDeptById(query.getDeptId());
        if (dept == null || "2".equals(dept.getDelFlag())) throw new ServiceException("所选单位不存在或已删除");
        query.setScopeName(dept.getDeptName() + "及下级单位");
    }
}
