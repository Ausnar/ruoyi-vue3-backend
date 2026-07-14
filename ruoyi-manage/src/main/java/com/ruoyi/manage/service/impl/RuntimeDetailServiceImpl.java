package com.ruoyi.manage.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.report.RuntimeDetailFirePointOption;
import com.ruoyi.manage.domain.report.RuntimeDetailDeviceOption;
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.domain.report.RuntimeDetailRow;
import com.ruoyi.manage.mapper.RuntimeDetailMapper;
import com.ruoyi.manage.report.RuntimeDetailExcelExporter;
import com.ruoyi.manage.report.RuntimeDetailPdfExporter;
import com.ruoyi.manage.service.IRuntimeDetailService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class RuntimeDetailServiceImpl implements IRuntimeDetailService
{
    private static final ZoneId REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int EXCEL_LIMIT = 100000;
    private static final int PDF_LIMIT = 1000;
    private static final int EXCEL_BATCH_SIZE = 5000;

    @Autowired
    private RuntimeDetailMapper runtimeDetailMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private RuntimeDetailExcelExporter excelExporter;

    @Autowired
    private RuntimeDetailPdfExporter pdfExporter;

    @Override
    @DataScope(deptAlias = "d", permission = "report:runtimeDetail:list")
    public List<RuntimeDetailRow> selectRuntimeDetailList(RuntimeDetailQuery query)
    {
        prepareQuery(query, true);
        return runtimeDetailMapper.selectRuntimeDetailList(query);
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:runtimeDetail:list")
    public List<RuntimeDetailFirePointOption> selectFirePointOptions(RuntimeDetailQuery query)
    {
        prepareQuery(query, false);
        List<RuntimeDetailFirePointOption> values = runtimeDetailMapper.selectFirePointOptions(query);
        return values == null ? Collections.<RuntimeDetailFirePointOption>emptyList() : values;
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:runtimeDetail:list")
    public List<RuntimeDetailDeviceOption> selectDeviceOptions(RuntimeDetailQuery query)
    {
        prepareQuery(query, false);
        List<RuntimeDetailDeviceOption> values = runtimeDetailMapper.selectDeviceOptions(query);
        return values == null ? Collections.<RuntimeDetailDeviceOption>emptyList() : values;
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:runtimeDetail:export")
    public void exportExcel(HttpServletResponse response, RuntimeDetailQuery query)
    {
        prepareQuery(query, true);
        long total = runtimeDetailMapper.countRuntimeDetails(query);
        if (total > EXCEL_LIMIT)
        {
            throw new ServiceException("当前筛选结果共" + total + "条，超过Excel单次导出上限" + EXCEL_LIMIT + "条，请缩小单位或消防点范围");
        }
        excelExporter.export(response, query, total, new RuntimeDetailExcelExporter.BatchLoader()
        {
            @Override
            public List<RuntimeDetailRow> load(int offset, int limit)
            {
                query.setOffset(offset);
                query.setLimit(limit);
                return runtimeDetailMapper.selectRuntimeDetailExportBatch(query);
            }
        }, EXCEL_BATCH_SIZE);
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:runtimeDetail:export")
    public void exportPdf(HttpServletResponse response, RuntimeDetailQuery query)
    {
        prepareQuery(query, true);
        long total = runtimeDetailMapper.countRuntimeDetails(query);
        if (total > PDF_LIMIT)
        {
            throw new ServiceException("当前筛选结果共" + total + "条，超过PDF单次导出上限" + PDF_LIMIT + "条，请缩小单位或消防点范围");
        }
        query.setOffset(0);
        query.setLimit(PDF_LIMIT);
        List<RuntimeDetailRow> rows = runtimeDetailMapper.selectRuntimeDetailExportBatch(query);
        pdfExporter.export(response, query, rows == null ? Collections.<RuntimeDetailRow>emptyList() : rows);
    }

    private void prepareQuery(RuntimeDetailQuery query, boolean requireDevice)
    {
        if (query == null)
        {
            throw new ServiceException("设备运行明细查询参数不能为空");
        }
        normalizeDetailType(query);
        normalizePeriod(query);
        normalizeDept(query);
        if (requireDevice && query.getDeviceId() == null && !Boolean.TRUE.equals(query.getAllDevices()))
        {
            throw new ServiceException("请选择具体设备，或主动选择该单位全部设备");
        }
        if (query.getDeviceId() != null && query.getDeviceId() <= 0)
        {
            throw new ServiceException("所选设备无效");
        }
        query.setDeviceKeyword(StringUtils.trim(query.getDeviceKeyword()));
    }

    private void normalizeDetailType(RuntimeDetailQuery query)
    {
        String detailType = StringUtils.isBlank(query.getDetailType()) ? "sensor" : query.getDetailType().trim();
        if (!"sensor".equals(detailType) && !"gateway".equals(detailType))
        {
            throw new ServiceException("不支持的运行明细类型");
        }
        query.setDetailType(detailType);
    }

    private void normalizePeriod(RuntimeDetailQuery query)
    {
        String periodType = StringUtils.isBlank(query.getPeriodType()) ? "day" : query.getPeriodType().trim();
        if (!"day".equals(periodType) && !"week".equals(periodType) && !"month".equals(periodType))
        {
            throw new ServiceException("统计周期仅支持日、周、月");
        }

        LocalDate anchor;
        try
        {
            anchor = StringUtils.isBlank(query.getPeriodDate())
                    ? LocalDate.now(REPORT_ZONE) : LocalDate.parse(query.getPeriodDate(), DATE);
        }
        catch (DateTimeParseException e)
        {
            throw new ServiceException("统计日期格式应为yyyy-MM-dd");
        }

        LocalDate start = periodStart(periodType, anchor);
        LocalDate currentStart = periodStart(periodType, LocalDate.now(REPORT_ZONE));
        if (start.isAfter(currentStart))
        {
            throw new ServiceException("不能查询未来统计周期");
        }
        LocalDate end = periodEnd(periodType, start);
        query.setPeriodType(periodType);
        query.setPeriodDate(start.format(DATE));
        query.setBeginTime(Date.from(start.atStartOfDay(REPORT_ZONE).toInstant()));
        query.setEndTime(Date.from(end.atStartOfDay(REPORT_ZONE).toInstant()));
        query.setPeriodLabel(periodLabel(periodType, start, end));
    }

    private LocalDate periodStart(String periodType, LocalDate anchor)
    {
        if ("week".equals(periodType))
        {
            return anchor.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        }
        if ("month".equals(periodType))
        {
            return anchor.withDayOfMonth(1);
        }
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
        if ("week".equals(periodType))
        {
            return start.format(DISPLAY_DATE) + " 至 " + end.minusDays(1).format(DISPLAY_DATE);
        }
        return start.format(DISPLAY_DATE);
    }

    private void normalizeDept(RuntimeDetailQuery query)
    {
        if (query.getDeptId() == null)
        {
            throw new ServiceException("请选择所属单位后再查询设备运行明细");
        }
        if (!SecurityUtils.isAdmin()) sysDeptService.checkDeptDataScope(query.getDeptId());
        SysDept dept = sysDeptService.selectDeptById(query.getDeptId());
        if (dept == null || "2".equals(dept.getDelFlag()))
        {
            throw new ServiceException("所选单位不存在或已删除");
        }
        query.setScopeName(dept.getDeptName() + "及下级单位");
    }
}
