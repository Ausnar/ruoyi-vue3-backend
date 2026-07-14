package com.ruoyi.manage.report;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.report.DeviceOperationReportPreview;

@Component
public class DeviceOperationReportExcelExporter
{
    private static final int COLUMN_COUNT = 8;

    public void export(HttpServletResponse response, DeviceOperationReportPreview preview)
    {
        String fileName = safeName(preview.getUnitInfo().getDeptName()) + "-设备运行" + preview.getPeriodTypeName() + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response, preview);
        }
        catch (IOException e)
        {
            throw new ServiceException("生成设备运行报告Excel失败：" + e.getMessage());
        }
    }

    private void write(HttpServletResponse response, DeviceOperationReportPreview preview) throws IOException
    {
        try (XSSFWorkbook workbook = new XSSFWorkbook())
        {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("设备运行报告");
            Styles styles = createStyles(workbook);
            configureSheet(sheet);

            int rowIndex = 0;
            rowIndex = title(sheet, rowIndex, preview, styles);
            rowIndex = section(sheet, rowIndex, "一、单位与统计周期", styles);
            rowIndex = pairs(sheet, rowIndex, styles,
                    "单位名称", text(preview.getUnitInfo().getDeptName()), "上级单位", text(preview.getUnitInfo().getParentDeptName()),
                    "归属地区", area(preview.getUnitInfo()), "统计范围", text(preview.getScopeName()),
                    "报告周期", text(preview.getPeriodLabel()), "报告类型", text(preview.getPeriodTypeName()));

            DeviceOperationReportPreview.AssetOverview assets = preview.getAssetOverview();
            rowIndex = section(sheet, rowIndex + 1, "二、设备资产概况", styles);
            rowIndex = metrics(sheet, rowIndex, styles,
                    new String[] { "消防点", "网关", "传感器", "灭火器" },
                    new Object[] { assets.getFirePointCount(), assets.getGatewayCount(), assets.getSensorCount(), assets.getExtinguisherCount() });

            DeviceOperationReportPreview.SensorSummary sensor = preview.getSensorSummary();
            rowIndex = section(sheet, rowIndex + 1, "三、传感器采样概况", styles);
            rowIndex = metricTable(sheet, rowIndex, styles, new String[][] {
                    { "纳入传感器", text(sensor.getSensorCount()), "统计范围内有效传感器数量" },
                    { "有采样传感器", text(sensor.getReportingSensorCount()), "本周期至少产生一条历史采样" },
                    { "无采样传感器", text(sensor.getNoSampleSensorCount()), "本周期未产生历史采样" },
                    { "历史采样总数", text(sensor.getSampleCount()), "本周期传感器历史记录数量" },
                    { "有效压力样本", text(sensor.getValidPressureCount()), "压力值位于0至2000范围" },
                    { "压力脏值", text(sensor.getInvalidPressureCount()), "压力小于0或大于2000，仅作数据质量统计" },
                    { "压力缺失样本", text(sensor.getMissingPressureCount()), "历史记录存在但压力为空" },
                    { "平均压力(MPa)", decimal(sensor.getAvgPressure()), "仅使用有效压力样本计算" },
                    { "平均温度(℃)", decimal(sensor.getAvgTemperature()), "本周期非空温度样本平均值" },
                    { "平均电量(%)", decimal(sensor.getAvgBatteryLevel()), "本周期非空电量样本平均值" }
            });

            DeviceOperationReportPreview.GatewaySummary gateway = preview.getGatewaySummary();
            rowIndex = section(sheet, rowIndex + 1, "四、网关定位数据概况", styles);
            rowIndex = metricTable(sheet, rowIndex, styles, new String[][] {
                    { "纳入网关", text(gateway.getGatewayCount()), "统计范围内有效网关数量" },
                    { "有GPS记录网关", text(gateway.getReportingGatewayCount()), "本周期至少产生一条GPS历史记录" },
                    { "无GPS记录网关", text(gateway.getNoGpsGatewayCount()), "本周期未产生GPS历史记录" },
                    { "GPS记录总数", text(gateway.getGpsRecordCount()), "本周期网关GPS历史记录数量" }
            });

            DeviceOperationReportPreview.ExtinguisherSummary extinguisher = preview.getExtinguisherSummary();
            rowIndex = section(sheet, rowIndex + 1, "五、灭火器资料概况", styles);
            rowIndex = metricTable(sheet, rowIndex, styles, new String[][] {
                    { "灭火器总数", text(extinguisher.getExtinguisherCount()), "统计范围内有效灭火器数量" },
                    { "本周期已同步", text(extinguisher.getSyncedCount()), "最后同步时间落在本周期" },
                    { "业务资料完整", text(extinguisher.getProfileCompleteCount()), "生产日期、类型、形式、标准及温度范围齐全" },
                    { "业务资料不完整", text(extinguisher.getProfileIncompleteCount()), "上述资料至少一项缺失" },
                    { "已绑定传感器", text(extinguisher.getBoundSensorCount()), "已建立灭火器与传感器关联" },
                    { "未绑定传感器", text(extinguisher.getUnboundSensorCount()), "尚未建立灭火器与传感器关联" }
            });

            DeviceOperationReportPreview.FirePointSummary firePoint = preview.getFirePointSummary();
            rowIndex = section(sheet, rowIndex + 1, "六、消防点快照概况", styles);
            rowIndex = metricTable(sheet, rowIndex, styles, new String[][] {
                    { "消防点总数", text(firePoint.getFirePointCount()), "统计范围内有效消防点数量" },
                    { "有快照消防点", text(firePoint.getSnapshotCoveredCount()), "本周期至少产生一条设备快照" },
                    { "无快照消防点", text(firePoint.getNoSnapshotCount()), "本周期未产生设备快照" },
                    { "设备快照总数", text(firePoint.getSnapshotCount()), "本周期消防点设备快照数量" },
                    { "已设置应配数量", text(firePoint.getExpectedConfiguredCount()), "应配灭火器数量大于0的消防点" }
            });

            rowIndex = section(sheet, rowIndex + 1, "七、统计口径说明", styles);
            Row note = sheet.createRow(rowIndex);
            note.setHeightInPoints(62);
            merge(sheet, rowIndex, 0, COLUMN_COUNT - 1);
            set(note, 0, "本报告仅汇总普通设备运行和数据质量信息，不在生成时临时判定低压、高压、低电量、温度异常等预警。预警生成、持续覆盖、自动恢复和状态流转由设备预警模块负责；待其历史口径稳定后，本报告再统一读取预警结果。", styles.note);

            workbook.write(response.getOutputStream());
        }
    }

    private int title(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, DeviceOperationReportPreview preview, Styles styles)
    {
        Row title = sheet.createRow(rowIndex++);
        title.setHeightInPoints(34);
        merge(sheet, title.getRowNum(), 0, COLUMN_COUNT - 1);
        set(title, 0, preview.getReportTitle(), styles.title);
        Row meta = sheet.createRow(rowIndex++);
        meta.setHeightInPoints(22);
        merge(sheet, meta.getRowNum(), 0, 3);
        merge(sheet, meta.getRowNum(), 4, COLUMN_COUNT - 1);
        set(meta, 0, "统计周期：" + preview.getPeriodLabel(), styles.meta);
        set(meta, 4, "生成时间：" + preview.getGeneratedTime(), styles.metaRight);
        return rowIndex;
    }

    private int section(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, String value, Styles styles)
    {
        Row row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(23);
        merge(sheet, row.getRowNum(), 0, COLUMN_COUNT - 1);
        set(row, 0, value, styles.section);
        return rowIndex;
    }

    private int pairs(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, Styles styles, String... values)
    {
        for (int i = 0; i < values.length; i += 4)
        {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(22);
            merge(sheet, row.getRowNum(), 1, 3);
            merge(sheet, row.getRowNum(), 5, 7);
            set(row, 0, values[i], styles.label);
            set(row, 1, values[i + 1], styles.value);
            set(row, 4, values[i + 2], styles.label);
            set(row, 5, values[i + 3], styles.value);
        }
        return rowIndex;
    }

    private int metrics(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, Styles styles, String[] headers, Object[] values)
    {
        Row header = sheet.createRow(rowIndex++);
        Row value = sheet.createRow(rowIndex++);
        header.setHeightInPoints(22);
        value.setHeightInPoints(28);
        for (int i = 0; i < headers.length; i++)
        {
            int column = i * 2;
            merge(sheet, header.getRowNum(), column, column + 1);
            merge(sheet, value.getRowNum(), column, column + 1);
            set(header, column, headers[i], styles.header);
            set(value, column, text(values[i]), styles.metric);
        }
        return rowIndex;
    }

    private int metricTable(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, Styles styles, String[][] rows)
    {
        Row header = sheet.createRow(rowIndex++);
        merge(sheet, header.getRowNum(), 0, 2);
        merge(sheet, header.getRowNum(), 3, 4);
        merge(sheet, header.getRowNum(), 5, 7);
        set(header, 0, "统计项", styles.header);
        set(header, 3, "统计值", styles.header);
        set(header, 5, "口径说明", styles.header);
        for (String[] values : rows)
        {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(22);
            merge(sheet, row.getRowNum(), 0, 2);
            merge(sheet, row.getRowNum(), 3, 4);
            merge(sheet, row.getRowNum(), 5, 7);
            set(row, 0, values[0], styles.value);
            set(row, 3, values[1], styles.valueCenter);
            set(row, 5, values[2], styles.value);
        }
        return rowIndex;
    }

    private Styles createStyles(XSSFWorkbook workbook)
    {
        Styles styles = new Styles();
        styles.title = style(workbook, 16, true, HorizontalAlignment.CENTER, IndexedColors.WHITE, true, false);
        styles.meta = style(workbook, 10, false, HorizontalAlignment.LEFT, IndexedColors.WHITE, true, false);
        styles.metaRight = style(workbook, 10, false, HorizontalAlignment.RIGHT, IndexedColors.WHITE, true, false);
        styles.section = style(workbook, 11, true, HorizontalAlignment.LEFT, IndexedColors.GREY_25_PERCENT, true, false);
        styles.header = style(workbook, 10, true, HorizontalAlignment.CENTER, IndexedColors.GREY_25_PERCENT, true, false);
        styles.label = style(workbook, 10, true, HorizontalAlignment.LEFT, IndexedColors.GREY_25_PERCENT, true, false);
        styles.value = style(workbook, 10, false, HorizontalAlignment.LEFT, IndexedColors.WHITE, true, false);
        styles.valueCenter = style(workbook, 10, false, HorizontalAlignment.CENTER, IndexedColors.WHITE, true, false);
        styles.metric = style(workbook, 14, true, HorizontalAlignment.CENTER, IndexedColors.WHITE, true, false);
        styles.note = style(workbook, 10, false, HorizontalAlignment.LEFT, IndexedColors.WHITE, true, true);
        return styles;
    }

    private CellStyle style(XSSFWorkbook workbook, int size, boolean bold, HorizontalAlignment alignment,
            IndexedColors fill, boolean border, boolean wrap)
    {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) size);
        font.setBold(bold);
        style.setFont(font);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(wrap);
        if (fill != IndexedColors.WHITE)
        {
            style.setFillForegroundColor(fill.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (border)
        {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.BLACK.getIndex());
            style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
            style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
            style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        }
        return style;
    }

    private void configureSheet(org.apache.poi.ss.usermodel.Sheet sheet)
    {
        int[] widths = { 15, 15, 15, 15, 15, 15, 15, 18 };
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
        sheet.setDisplayGridlines(false);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.LeftMargin, 0.35);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.RightMargin, 0.35);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
    }

    private void set(Row row, int column, String value, CellStyle style)
    {
        Cell cell = row.getCell(column);
        if (cell == null) cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);

        CellRangeAddress mergedRegion = findMergedRegion(row, column);
        if (mergedRegion == null) return;
        for (int mergedColumn = mergedRegion.getFirstColumn(); mergedColumn <= mergedRegion.getLastColumn(); mergedColumn++)
        {
            Cell mergedCell = row.getCell(mergedColumn);
            if (mergedCell == null) mergedCell = row.createCell(mergedColumn);
            mergedCell.setCellStyle(style);
        }
    }

    private CellRangeAddress findMergedRegion(Row row, int column)
    {
        org.apache.poi.ss.usermodel.Sheet sheet = row.getSheet();
        for (CellRangeAddress region : sheet.getMergedRegions())
        {
            if (region.isInRange(row.getRowNum(), column)) return region;
        }
        return null;
    }

    private void merge(org.apache.poi.ss.usermodel.Sheet sheet, int row, int first, int last)
    {
        if (last > first) sheet.addMergedRegion(new CellRangeAddress(row, row, first, last));
    }

    private String area(DeviceOperationReportPreview.UnitInfo unit)
    {
        StringBuilder value = new StringBuilder();
        append(value, unit.getProvince());
        append(value, unit.getCity());
        append(value, unit.getArea());
        return value.length() == 0 ? "-" : value.toString();
    }

    private void append(StringBuilder value, String part)
    {
        if (StringUtils.isBlank(part)) return;
        if (value.length() > 0) value.append(" / ");
        value.append(part);
    }

    private String decimal(Object value)
    {
        return value == null ? "-" : String.valueOf(value);
    }

    private String text(Object value)
    {
        return value == null || StringUtils.isBlank(String.valueOf(value)) ? "-" : String.valueOf(value);
    }

    private String safeName(String value)
    {
        return text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static class Styles
    {
        CellStyle title;
        CellStyle meta;
        CellStyle metaRight;
        CellStyle section;
        CellStyle header;
        CellStyle label;
        CellStyle value;
        CellStyle valueCenter;
        CellStyle metric;
        CellStyle note;
    }
}
