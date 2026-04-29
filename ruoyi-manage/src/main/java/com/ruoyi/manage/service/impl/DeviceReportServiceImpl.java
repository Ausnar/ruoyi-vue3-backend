package com.ruoyi.manage.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.report.DeviceReportOverview;
import com.ruoyi.manage.domain.report.DeviceReportPreview;
import com.ruoyi.manage.domain.report.DeviceReportQuery;
import com.ruoyi.manage.domain.report.DeviceReportRiskItem;
import com.ruoyi.manage.domain.report.DeviceReportUnitStat;
import com.ruoyi.manage.mapper.DeviceReportMapper;
import com.ruoyi.manage.service.IDeviceReportService;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 设备报告服务实现。
 */
@Service
public class DeviceReportServiceImpl implements IDeviceReportService
{
    private static final ZoneId REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private DeviceReportMapper deviceReportMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    @DataScope(deptAlias = "d", permission = "manage:deviceReport:list")
    public DeviceReportPreview preview(DeviceReportQuery query)
    {
        prepareQuery(query);
        return buildPreview(query);
    }

    @Override
    @DataScope(deptAlias = "d", permission = "manage:deviceReport:export")
    public void export(HttpServletResponse response, DeviceReportQuery query)
    {
        prepareQuery(query);
        DeviceReportPreview preview = buildPreview(query);
        writeWord(response, preview);
    }

    private DeviceReportPreview buildPreview(DeviceReportQuery query)
    {
        DeviceReportOverview overview = deviceReportMapper.selectOverview(query);
        if (overview == null)
        {
            overview = new DeviceReportOverview();
        }

        List<DeviceReportRiskItem> riskItems = new ArrayList<>();
        riskItems.addAll(deviceReportMapper.selectFirePointRiskItems(query));
        riskItems.addAll(deviceReportMapper.selectSensorRiskItems(query));
        riskItems.addAll(deviceReportMapper.selectGatewayRiskItems(query));
        riskItems.addAll(deviceReportMapper.selectExtinguisherRiskItems(query));
        if (riskItems.size() > 50)
        {
            riskItems = riskItems.subList(0, 50);
        }

        DeviceReportPreview preview = new DeviceReportPreview();
        preview.setReportType(query.getReportType());
        preview.setReportTypeName(query.getReportTypeName());
        preview.setPeriodText(query.getPeriodText());
        preview.setScopeName(query.getScopeName());
        preview.setGeneratedTime(DATE_TIME_FORMATTER.format(LocalDateTime.now(REPORT_ZONE)));
        preview.setOverview(overview);
        preview.setUnitStats(deviceReportMapper.selectUnitStats(query));
        preview.setRiskItems(riskItems);
        return preview;
    }

    private void prepareQuery(DeviceReportQuery query)
    {
        if (query == null)
        {
            throw new ServiceException("设备报告查询参数不能为空");
        }
        normalizeReportType(query);
        normalizePeriod(query);
        normalizeScope(query);
    }

    private void normalizeReportType(DeviceReportQuery query)
    {
        String reportType = StringUtils.defaultIfBlank(query.getReportType(), "month");
        if (!"day".equals(reportType) && !"week".equals(reportType) && !"month".equals(reportType))
        {
            throw new ServiceException("报告类型仅支持日报、周报、月报");
        }
        query.setReportType(reportType);
        if ("day".equals(reportType))
        {
            query.setReportTypeName("日报");
        }
        else if ("week".equals(reportType))
        {
            query.setReportTypeName("周报");
        }
        else
        {
            query.setReportTypeName("月报");
        }
    }

    private void normalizePeriod(DeviceReportQuery query)
    {
        LocalDate today = LocalDate.now(REPORT_ZONE);
        LocalDate startDate;
        LocalDate endDate;
        if (StringUtils.isNotBlank(query.getStartDate()) && StringUtils.isNotBlank(query.getEndDate()))
        {
            startDate = LocalDate.parse(query.getStartDate(), DATE_FORMATTER);
            endDate = LocalDate.parse(query.getEndDate(), DATE_FORMATTER);
        }
        else if ("day".equals(query.getReportType()))
        {
            startDate = today;
            endDate = today;
        }
        else if ("week".equals(query.getReportType()))
        {
            startDate = today.with(DayOfWeek.MONDAY);
            endDate = today.with(DayOfWeek.SUNDAY);
        }
        else
        {
            YearMonth currentMonth = YearMonth.from(today);
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        if (endDate.isBefore(startDate))
        {
            throw new ServiceException("报告结束日期不能早于开始日期");
        }

        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTimeExclusive = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIN);
        LocalDateTime expireSoonEndTime = endDateTimeExclusive.plusDays(30);
        query.setStartDate(startDate.format(DATE_FORMATTER));
        query.setEndDate(endDate.format(DATE_FORMATTER));
        query.setStartTime(toDate(startDateTime));
        query.setEndTimeExclusive(toDate(endDateTimeExclusive));
        query.setExpireSoonEndTime(toDate(expireSoonEndTime));
        query.setPeriodText(startDate.format(DATE_FORMATTER) + " 至 " + endDate.format(DATE_FORMATTER));
    }

    private void normalizeScope(DeviceReportQuery query)
    {
        if (query.getDeptId() == null)
        {
            query.setScopeName("全部授权单位");
            return;
        }
        if (!SecurityUtils.isAdmin())
        {
            sysDeptService.checkDeptDataScope(query.getDeptId());
        }
        SysDept dept = sysDeptService.selectDeptById(query.getDeptId());
        query.setScopeName(dept == null ? "指定单位及下级单位" : dept.getDeptName() + "及下级单位");
    }

    private Date toDate(LocalDateTime localDateTime)
    {
        return Date.from(localDateTime.atZone(REPORT_ZONE).toInstant());
    }

    private void writeWord(HttpServletResponse response, DeviceReportPreview preview)
    {
        String fileName = "设备运行" + preview.getReportTypeName() + "-" + preview.getPeriodText().replace(" 至 ", "_") + ".docx";
        try (XWPFDocument document = new XWPFDocument())
        {
            writeTitle(document, "设备运行" + preview.getReportTypeName());
            writeParagraph(document, "统计周期：" + preview.getPeriodText());
            writeParagraph(document, "统计范围：" + preview.getScopeName());
            writeParagraph(document, "生成时间：" + preview.getGeneratedTime());

            writeHeading(document, "一、综合总览");
            DeviceReportOverview overview = preview.getOverview();
            writeTable(document,
                    new String[] { "消防点数", "网关数", "传感器数", "灭火器数", "风险设备数", "灭火器不足消防点" },
                    new String[][] {
                            {
                                    text(overview.getFirePointCount()),
                                    text(overview.getGatewayCount()),
                                    text(overview.getSensorCount()),
                                    text(overview.getExtinguisherCount()),
                                    text(overview.getRiskDeviceCount()),
                                    text(overview.getFirePointExtinguisherShortageCount())
                            }
                    });

            writeHeading(document, "二、单位分布 TOP10");
            writeUnitTable(document, preview.getUnitStats());

            writeHeading(document, "三、传感器专题");
            writeTable(document,
                    new String[] { "正常", "异常", "离线", "持续低电量", "持续低压力", "持续高压力", "压力脏值", "有效采样数" },
                    new String[][] {
                            {
                                    text(overview.getSensorNormalCount()),
                                    text(overview.getSensorAbnormalCount()),
                                    text(overview.getSensorOfflineCount()),
                                    text(overview.getSensorLowBatteryCount()),
                                    text(overview.getSensorLowPressureCount()),
                                    text(overview.getSensorHighPressureCount()),
                                    text(overview.getSensorInvalidPressureCount()),
                                    text(overview.getHistorySampleCount())
                            }
                    });
            writeParagraph(document, "有效采样均值：压力 " + text(overview.getAvgPressure()) + "，温度 "
                    + text(overview.getAvgTemperature()) + "，电量 " + text(overview.getAvgBatteryLevel())
                    + "。低/高压力与低电量按最近连续3次有效采样确认；压力统计已剔除小于 0 或大于 2000 的脏值。");

            writeHeading(document, "四、灭火器专题");
            writeTable(document,
                    new String[] { "正常", "30天内到期/报废", "已过期/报废", "未绑定传感器", "缺失归属", "数量不足消防点" },
                    new String[][] {
                            {
                                    text(overview.getExtinguisherNormalCount()),
                                    text(overview.getExtinguisherExpiringSoonCount()),
                                    text(overview.getExtinguisherExpiredCount()),
                                    text(overview.getExtinguisherUnboundSensorCount()),
                                    text(overview.getExtinguisherMissingDeptCount()),
                                    text(overview.getFirePointExtinguisherShortageCount())
                            }
                    });
            writeParagraph(document, "灭火器数量不足按消防点应配数量与 SDK 同步后的实测灭火器数量判断，仅当最近连续3次消防点设备数量快照均低于应配数量时确认。");

            writeHeading(document, "五、网关专题");
            writeTable(document,
                    new String[] { "正常", "异常", "离线", "未绑定消防点", "缺失归属" },
                    new String[][] {
                            {
                                    text(overview.getGatewayNormalCount()),
                                    text(overview.getGatewayAbnormalCount()),
                                    text(overview.getGatewayOfflineCount()),
                                    text(overview.getGatewayUnboundFirePointCount()),
                                    text(overview.getGatewayMissingDeptCount())
                            }
                    });

            writeHeading(document, "六、数据质量提示");
            writeParagraph(document, "报告期内传感器历史压力脏值：" + text(overview.getHistoryInvalidPressureCount())
                    + " 条；未来采样时间：" + text(overview.getHistoryFutureCount()) + " 条。缺失本地归属的数据不会直接用于普通角色越权统计。");

            writeHeading(document, "七、重点风险清单");
            writeRiskTable(document, preview.getRiskItems());

            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
            document.write(response.getOutputStream());
        }
        catch (IOException e)
        {
            throw new ServiceException("生成设备报告失败：" + e.getMessage());
        }
    }

    private void writeTitle(XWPFDocument document, String title)
    {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(20);
        run.setText(title);
    }

    private void writeHeading(XWPFDocument document, String heading)
    {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(true);
        run.setFontSize(14);
        run.setText(heading);
    }

    private void writeParagraph(XWPFDocument document, String text)
    {
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setFontSize(11);
        run.setText(text);
    }

    private void writeUnitTable(XWPFDocument document, List<DeviceReportUnitStat> rows)
    {
        String[] headers = { "单位", "消防点", "网关", "传感器", "灭火器", "设备合计", "风险数" };
        String[][] data = new String[rows == null ? 0 : rows.size()][headers.length];
        if (rows != null)
        {
            for (int i = 0; i < rows.size(); i++)
            {
                DeviceReportUnitStat row = rows.get(i);
                data[i] = new String[] {
                        text(row.getDeptName()),
                        text(row.getFirePointCount()),
                        text(row.getGatewayCount()),
                        text(row.getSensorCount()),
                        text(row.getExtinguisherCount()),
                        text(row.getTotalCount()),
                        text(row.getRiskCount())
                };
            }
        }
        writeTable(document, headers, data);
    }

    private void writeRiskTable(XWPFDocument document, List<DeviceReportRiskItem> rows)
    {
        String[] headers = { "类别", "风险类型", "等级", "归属单位", "对象", "说明" };
        String[][] data = new String[rows == null ? 0 : rows.size()][headers.length];
        if (rows != null)
        {
            for (int i = 0; i < rows.size(); i++)
            {
                DeviceReportRiskItem row = rows.get(i);
                data[i] = new String[] {
                        text(row.getCategory()),
                        text(row.getRiskType()),
                        text(row.getRiskLevel()),
                        text(row.getDeptName()),
                        text(row.getItemName()),
                        text(row.getDescription())
                };
            }
        }
        writeTable(document, headers, data);
    }

    private void writeTable(XWPFDocument document, String[] headers, String[][] data)
    {
        int rowCount = Math.max(1, data.length) + 1;
        XWPFTable table = document.createTable(rowCount, headers.length);
        XWPFTableRow headerRow = table.getRow(0);
        for (int i = 0; i < headers.length; i++)
        {
            setCellText(headerRow.getCell(i), headers[i], true);
        }
        if (data.length == 0)
        {
            setCellText(table.getRow(1).getCell(0), "暂无数据", false);
            return;
        }
        for (int rowIndex = 0; rowIndex < data.length; rowIndex++)
        {
            XWPFTableRow row = table.getRow(rowIndex + 1);
            for (int colIndex = 0; colIndex < headers.length; colIndex++)
            {
                setCellText(row.getCell(colIndex), data[rowIndex][colIndex], false);
            }
        }
    }

    private void setCellText(XWPFTableCell cell, String text, boolean bold)
    {
        cell.removeParagraph(0);
        XWPFParagraph paragraph = cell.addParagraph();
        XWPFRun run = paragraph.createRun();
        run.setBold(bold);
        run.setText(text(text));
    }

    private String text(Object value)
    {
        return value == null ? "-" : String.valueOf(value);
    }
}
