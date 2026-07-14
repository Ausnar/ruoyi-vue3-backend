package com.ruoyi.manage.report;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.domain.report.RuntimeDetailRow;

@Component
public class RuntimeDetailExcelExporter
{
    public interface BatchLoader
    {
        List<RuntimeDetailRow> load(int offset, int limit);
    }

    public void export(HttpServletResponse response, RuntimeDetailQuery query, long total,
            BatchLoader loader, int batchSize)
    {
        String typeName = "gateway".equals(query.getDetailType()) ? "网关定位明细" : "传感器运行明细";
        String fileName = safeName(query.getScopeName()) + "-" + safeName(query.getPeriodLabel()) + "-" + typeName + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response, query, total, loader, batchSize, typeName);
        }
        catch (IOException e)
        {
            throw new ServiceException("生成设备运行明细Excel失败：" + e.getMessage());
        }
    }

    private void write(HttpServletResponse response, RuntimeDetailQuery query, long total,
            BatchLoader loader, int batchSize, String typeName) throws IOException
    {
        SXSSFWorkbook workbook = new SXSSFWorkbook(200);
        workbook.setCompressTempFiles(true);
        try
        {
            Sheet sheet = workbook.createSheet(typeName);
            Styles styles = createStyles(workbook);
            String[] headers = headers(query.getDetailType());
            configureSheet(sheet, headers.length);

            Row title = sheet.createRow(0);
            title.setHeightInPoints(32);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
            cell(title, 0, query.getScopeName() + typeName, styles.title);

            Row meta = sheet.createRow(1);
            meta.setHeightInPoints(22);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));
            cell(meta, 0, "统计周期：" + query.getPeriodLabel() + "    记录数：" + total, styles.meta);

            Row header = sheet.createRow(2);
            header.setHeightInPoints(26);
            for (int i = 0; i < headers.length; i++) cell(header, i, headers[i], styles.header);

            int rowIndex = 3;
            int offset = 0;
            while (offset < total)
            {
                List<RuntimeDetailRow> rows = loader.load(offset, batchSize);
                if (rows == null) rows = Collections.emptyList();
                if (rows.isEmpty()) break;
                for (RuntimeDetailRow value : rows)
                {
                    Row row = sheet.createRow(rowIndex++);
                    row.setHeightInPoints(22);
                    String[] values = values(query.getDetailType(), value);
                    for (int i = 0; i < values.length; i++) cell(row, i, values[i], styles.body);
                }
                offset += rows.size();
                if (rows.size() < batchSize) break;
            }
            sheet.setAutoFilter(new CellRangeAddress(2, Math.max(2, rowIndex - 1), 0, headers.length - 1));
            workbook.write(response.getOutputStream());
        }
        finally
        {
            workbook.dispose();
            workbook.close();
        }
    }

    private String[] headers(String detailType)
    {
        if ("gateway".equals(detailType))
        {
            return new String[] { "GPS时间", "同步时间", "所属单位", "消防点", "TBoxID", "网关IMEI", "经度", "纬度" };
        }
        return new String[] { "采集时间", "所属单位", "消防点", "传感器编号", "网关编号", "压力(MPa)",
                "温度(℃)", "电量(%)", "信号强度(dBm)", "采样状态", "数据质量" };
    }

    private String[] values(String detailType, RuntimeDetailRow row)
    {
        if ("gateway".equals(detailType))
        {
            return new String[] { UnitDeviceReportFormat.dateTime(row.getRecordTime()),
                    UnitDeviceReportFormat.dateTime(row.getSyncTime()), text(row.getDeptName()),
                    text(row.getFirePointName()), text(row.getDeviceCode()), text(row.getGatewayImei()),
                    text(row.getLongitude()), text(row.getLatitude()) };
        }
        return new String[] { UnitDeviceReportFormat.dateTime(row.getRecordTime()), text(row.getDeptName()),
                text(row.getFirePointName()), text(row.getDeviceCode()), text(row.getGatewayCode()),
                text(row.getPressure()), text(row.getTemperature()), text(row.getBatteryLevel()),
                text(row.getSignalStrength()), UnitDeviceReportFormat.sensorStatus(row.getStatus()),
                quality(row.getDataQuality()) };
    }

    private void configureSheet(Sheet sheet, int columnCount)
    {
        sheet.createFreezePane(0, 3);
        sheet.setDisplayGridlines(false);
        sheet.setRepeatingRows(new CellRangeAddress(2, 2, 0, columnCount - 1));
        int[] widths = columnCount == 8
                ? new int[] { 20, 20, 26, 20, 16, 22, 16, 16 }
                : new int[] { 20, 26, 20, 20, 22, 14, 12, 11, 16, 12, 14 };
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
    }

    private Styles createStyles(SXSSFWorkbook workbook)
    {
        Styles styles = new Styles();
        styles.title = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setFontName("微软雅黑");
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleFont.setColor(IndexedColors.BLACK.getIndex());
        styles.title.setFont(titleFont);
        styles.title.setAlignment(HorizontalAlignment.CENTER);
        styles.title.setVerticalAlignment(VerticalAlignment.CENTER);

        styles.meta = workbook.createCellStyle();
        Font metaFont = workbook.createFont();
        metaFont.setFontName("微软雅黑");
        metaFont.setFontHeightInPoints((short) 10);
        metaFont.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
        styles.meta.setFont(metaFont);
        styles.meta.setAlignment(HorizontalAlignment.CENTER);
        styles.meta.setVerticalAlignment(VerticalAlignment.CENTER);

        styles.header = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setFontName("微软雅黑");
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.BLACK.getIndex());
        styles.header.setFont(headerFont);
        styles.header.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        styles.header.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        styles.header.setAlignment(HorizontalAlignment.CENTER);
        styles.header.setVerticalAlignment(VerticalAlignment.CENTER);
        border(styles.header);

        styles.body = workbook.createCellStyle();
        Font bodyFont = workbook.createFont();
        bodyFont.setFontName("微软雅黑");
        bodyFont.setFontHeightInPoints((short) 10);
        styles.body.setFont(bodyFont);
        styles.body.setVerticalAlignment(VerticalAlignment.CENTER);
        border(styles.body);
        return styles;
    }

    private void border(CellStyle style)
    {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private void cell(Row row, int column, String value, CellStyle style)
    {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String quality(String value)
    {
        if ("dirty_pressure".equals(value)) return "压力脏值";
        if ("missing".equals(value)) return "数据缺失";
        return "有效";
    }

    private String text(Object value)
    {
        return UnitDeviceReportFormat.text(value);
    }

    private String safeName(String value)
    {
        return text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static class Styles
    {
        private CellStyle title;
        private CellStyle meta;
        private CellStyle header;
        private CellStyle body;
    }
}
