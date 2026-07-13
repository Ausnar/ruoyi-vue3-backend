package com.ruoyi.manage.report;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.report.UnitDeviceReportPreview;

@Component
public class UnitDeviceReportExcelExporter
{
    private static final int COLUMN_COUNT = 12;
    private static final byte[] NAVY = new byte[] { 31, 55, 82 };
    private static final byte[] BLUE = new byte[] { 47, 111, (byte) 179 };
    private static final byte[] LIGHT_BLUE = new byte[] { (byte) 221, (byte) 235, (byte) 247 };

    public void export(HttpServletResponse response, UnitDeviceReportPreview preview)
    {
        String fileName = safeName(preview.getUnitInfo().getDeptName()) + "-单位设备报告.xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response.getOutputStream(), preview);
        }
        catch (IOException e)
        {
            throw new ServiceException("生成单位设备报告Excel失败：" + e.getMessage());
        }
    }

    void write(OutputStream output, UnitDeviceReportPreview preview) throws IOException
    {
        try (XSSFWorkbook workbook = new XSSFWorkbook())
        {
            Styles styles = createStyles(workbook);
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("单位设备报告");
            configureSheet(sheet);

            int rowIndex = 0;
            rowIndex = writeTitle(sheet, rowIndex, preview, styles);
            rowIndex = writeUnitInfo(sheet, rowIndex, preview, styles);
            rowIndex = writeOverview(sheet, rowIndex, preview, styles);
            rowIndex = writeFirePoints(sheet, rowIndex, preview, styles);
            rowIndex = writeGateways(sheet, rowIndex, preview, styles);
            rowIndex = writeSensors(sheet, rowIndex, preview, styles);
            rowIndex = writeExtinguishers(sheet, rowIndex, preview, styles);
            writeQualityNote(sheet, rowIndex, preview, styles);
            workbook.write(output);
        }
    }

    private int writeTitle(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        Row title = sheet.createRow(rowIndex++);
        title.setHeightInPoints(34);
        merge(sheet, title.getRowNum(), 0, COLUMN_COUNT - 1);
        cell(title, 0, preview.getReportTitle(), styles.title);

        Row meta = sheet.createRow(rowIndex++);
        meta.setHeightInPoints(24);
        merge(sheet, meta.getRowNum(), 0, 4);
        merge(sheet, meta.getRowNum(), 5, COLUMN_COUNT - 1);
        cell(meta, 0, "统计范围：" + preview.getScopeName(), styles.meta);
        cell(meta, 5, "生成时间：" + preview.getGeneratedTime(), styles.metaRight);
        return rowIndex + 1;
    }

    private int writeUnitInfo(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "一、单位基本信息", styles);
        UnitDeviceReportPreview.UnitInfo unit = preview.getUnitInfo();
        String[][] values = {
                { "单位名称", UnitDeviceReportFormat.text(unit.getDeptName()), "上级单位", UnitDeviceReportFormat.text(unit.getParentDeptName()) },
                { "归属地区", UnitDeviceReportFormat.area(unit.getProvince(), unit.getCity(), unit.getArea()), "单位状态", UnitDeviceReportFormat.unitStatus(unit.getStatus()) },
                { "负责人", UnitDeviceReportFormat.text(unit.getLeader()), "联系电话", UnitDeviceReportFormat.text(unit.getPhone()) },
                { "电子邮箱", UnitDeviceReportFormat.text(unit.getEmail()), "统计口径", "所选单位及下级单位" }
        };
        for (String[] value : values)
        {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(23);
            merge(sheet, row.getRowNum(), 1, 4);
            merge(sheet, row.getRowNum(), 6, COLUMN_COUNT - 1);
            cell(row, 0, value[0], styles.label);
            cell(row, 1, value[1], styles.value);
            cell(row, 5, value[2], styles.label);
            cell(row, 6, value[3], styles.value);
        }
        return rowIndex + 1;
    }

    private int writeOverview(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "二、设备概况", styles);
        UnitDeviceReportPreview.Overview o = preview.getOverview();
        String[] headers = { "下级单位", "消防点", "网关", "传感器", "灭火器", "未绑消防点网关", "未绑网关传感器", "未绑传感器灭火器" };
        String[] values = { text(o.getChildDeptCount()), text(o.getFirePointCount()), text(o.getGatewayCount()),
                text(o.getSensorCount()), text(o.getExtinguisherCount()), text(o.getGatewayUnboundFirePointCount()),
                text(o.getSensorUnboundGatewayCount()), text(o.getExtinguisherUnboundSensorCount()) };
        return simpleTable(sheet, rowIndex, headers, Collections.singletonList(values), styles) + 1;
    }

    private int writeFirePoints(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "三、消防点信息", styles);
        String[] headers = { "序号", "所属单位", "消防点名称", "消防点编号", "站点类型", "位置", "建筑物", "楼层", "应配灭火器", "实配灭火器", "传感器", "状态" };
        List<String[]> data = new ArrayList<>();
        int index = 1;
        for (UnitDeviceReportPreview.FirePointRow row : preview.getFirePoints())
        {
            data.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                    text(row.getFirePointCode()), UnitDeviceReportFormat.firePointType(row.getStationType()), text(row.getLocation()),
                    text(row.getBuilding()), text(row.getFloor()), text(row.getExpectedExtinguisherCount()),
                    text(row.getActualExtinguisherCount()), text(row.getActualSensorCount()),
                    UnitDeviceReportFormat.firePointStatus(row.getStatus()) });
        }
        return dynamicTable(sheet, rowIndex, headers, data, styles) + 1;
    }

    private int writeGateways(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "四、网关信息", styles);
        String[] headers = { "序号", "所属单位", "消防点", "TBoxID", "网关IMEI", "SIM", "状态", "最后在线时间", "最后同步时间" };
        List<String[]> data = new ArrayList<>();
        int index = 1;
        for (UnitDeviceReportPreview.GatewayRow row : preview.getGateways())
        {
            data.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                    text(row.getExternalTboxId()), text(row.getImei()), text(row.getSim()),
                    UnitDeviceReportFormat.gatewayStatus(row.getStatus()), UnitDeviceReportFormat.dateTime(row.getLastOnlineTime()),
                    UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
        }
        return dynamicTable(sheet, rowIndex, headers, data, styles) + 1;
    }

    private int writeSensors(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "五、传感器信息", styles);
        String[] headers = { "序号", "所属单位", "消防点", "传感器编号", "网关编号", "压力(MPa)", "温度(℃)", "电量(%)", "状态", "最后在线时间", "最后同步时间" };
        List<String[]> data = new ArrayList<>();
        int index = 1;
        for (UnitDeviceReportPreview.SensorRow row : preview.getSensors())
        {
            data.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                    text(row.getSensorCode()), text(row.getGatewayCode()), text(row.getPressure()), text(row.getTemperature()),
                    text(row.getBatteryLevel()), UnitDeviceReportFormat.sensorStatus(row.getStatus()),
                    UnitDeviceReportFormat.dateTime(row.getLastOnlineTime()), UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
        }
        return dynamicTable(sheet, rowIndex, headers, data, styles) + 1;
    }

    private int writeExtinguishers(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "六、灭火器信息", styles);
        String[] headers = { "序号", "所属单位", "消防点", "标志铭码", "产品/规格", "类型/形式", "传感器编号", "生产日期", "到期日期", "状态", "最后同步时间" };
        List<String[]> data = new ArrayList<>();
        int index = 1;
        for (UnitDeviceReportPreview.ExtinguisherRow row : preview.getExtinguishers())
        {
            data.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                    text(row.getLabelCode()), join(row.getProductName(), row.getSpecification()),
                    join(UnitDeviceReportFormat.extinguisherType(row.getExtinguisherType()),
                            UnitDeviceReportFormat.extinguisherForm(row.getExtinguisherForm())),
                    text(row.getSensorCode()), UnitDeviceReportFormat.date(row.getProductionDate()),
                    UnitDeviceReportFormat.date(row.getExpiryDate()), UnitDeviceReportFormat.extinguisherStatus(row.getStatus()),
                    UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
        }
        return dynamicTable(sheet, rowIndex, headers, data, styles) + 1;
    }

    private void writeQualityNote(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex,
            UnitDeviceReportPreview preview, Styles styles)
    {
        rowIndex = sectionTitle(sheet, rowIndex, "七、数据口径说明", styles);
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(42);
        merge(sheet, rowIndex, 0, COLUMN_COUNT - 1);
        cell(row, 0, "本报告按生成时点的当前有效主数据统计。网关最后在线时间当前不一定来自SDK真实在线时间，仅作资料展示；缺失所属单位的数据不进入普通用户授权范围统计。", styles.note);
    }

    private int simpleTable(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, String[] headers,
            List<String[]> data, Styles styles)
    {
        Row header = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.length; i++) cell(header, i, headers[i], styles.header);
        for (String[] values : data)
        {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < values.length; i++) cell(row, i, values[i], styles.center);
        }
        return rowIndex;
    }

    private int dynamicTable(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, String[] headers,
            List<String[]> data, Styles styles)
    {
        Row header = sheet.createRow(rowIndex++);
        header.setHeightInPoints(28);
        for (int i = 0; i < headers.length; i++) cell(header, i, headers[i], styles.header);
        if (data.isEmpty())
        {
            Row empty = sheet.createRow(rowIndex++);
            merge(sheet, empty.getRowNum(), 0, headers.length - 1);
            cell(empty, 0, "暂无数据", styles.empty);
            return rowIndex;
        }
        for (String[] values : data)
        {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(24);
            for (int i = 0; i < values.length; i++) cell(row, i, values[i], i == 0 ? styles.center : styles.body);
        }
        return rowIndex;
    }

    private int sectionTitle(org.apache.poi.ss.usermodel.Sheet sheet, int rowIndex, String title, Styles styles)
    {
        Row row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(26);
        merge(sheet, row.getRowNum(), 0, COLUMN_COUNT - 1);
        cell(row, 0, title, styles.section);
        return rowIndex;
    }

    private void configureSheet(org.apache.poi.ss.usermodel.Sheet sheet)
    {
        sheet.setDisplayGridlines(false);
        sheet.setAutobreaks(true);
        sheet.createFreezePane(0, 3);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.LeftMargin, 0.25);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.RightMargin, 0.25);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.TopMargin, 0.45);
        sheet.setMargin(org.apache.poi.ss.usermodel.Sheet.BottomMargin, 0.45);
        PrintSetup print = sheet.getPrintSetup();
        print.setLandscape(true);
        print.setPaperSize(PrintSetup.A4_PAPERSIZE);
        print.setFitWidth((short) 1);
        print.setFitHeight((short) 0);
        sheet.setFitToPage(true);
        int[] widths = { 8, 22, 21, 18, 19, 18, 17, 16, 16, 17, 21, 12 };
        for (int i = 0; i < widths.length; i++) sheet.setColumnWidth(i, widths[i] * 256);
    }

    private Styles createStyles(XSSFWorkbook workbook)
    {
        Styles styles = new Styles();
        styles.title = style(workbook, 18, true, HorizontalAlignment.CENTER, null, false);
        styles.meta = style(workbook, 10, false, HorizontalAlignment.LEFT, null, false);
        styles.metaRight = style(workbook, 10, false, HorizontalAlignment.RIGHT, null, false);
        styles.section = style(workbook, 12, true, HorizontalAlignment.LEFT, BLUE, true);
        styles.header = style(workbook, 9, true, HorizontalAlignment.CENTER, NAVY, true);
        styles.label = style(workbook, 9, true, HorizontalAlignment.CENTER, LIGHT_BLUE, true);
        styles.value = style(workbook, 9, false, HorizontalAlignment.LEFT, null, true);
        styles.body = style(workbook, 8, false, HorizontalAlignment.LEFT, null, true);
        styles.center = style(workbook, 8, false, HorizontalAlignment.CENTER, null, true);
        styles.empty = style(workbook, 9, false, HorizontalAlignment.CENTER, null, true);
        styles.note = style(workbook, 9, false, HorizontalAlignment.LEFT, LIGHT_BLUE, true);
        return styles;
    }

    private CellStyle style(XSSFWorkbook workbook, int size, boolean bold, HorizontalAlignment alignment,
            byte[] fill, boolean border)
    {
        XSSFCellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Microsoft YaHei");
        font.setFontHeightInPoints((short) size);
        font.setBold(bold);
        if (fill != null && (Arrays.equals(fill, NAVY) || Arrays.equals(fill, BLUE)))
        {
            font.setColor(IndexedColors.WHITE.getIndex());
        }
        style.setFont(font);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        if (fill != null)
        {
            style.setFillForegroundColor(new XSSFColor(fill, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        if (border)
        {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
        return style;
    }

    private void cell(Row row, int index, String value, CellStyle style)
    {
        Cell cell = row.getCell(index);
        if (cell == null) cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void merge(org.apache.poi.ss.usermodel.Sheet sheet, int row, int firstCol, int lastCol)
    {
        if (lastCol > firstCol) sheet.addMergedRegion(new CellRangeAddress(row, row, firstCol, lastCol));
    }

    private String join(String first, String second)
    {
        String a = UnitDeviceReportFormat.text(first);
        String b = UnitDeviceReportFormat.text(second);
        if ("-".equals(a)) return b;
        if ("-".equals(b)) return a;
        return a + " / " + b;
    }

    private String text(Object value)
    {
        return UnitDeviceReportFormat.text(value);
    }

    private String safeName(String value)
    {
        return UnitDeviceReportFormat.text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static class Styles
    {
        private CellStyle title;
        private CellStyle meta;
        private CellStyle metaRight;
        private CellStyle section;
        private CellStyle header;
        private CellStyle label;
        private CellStyle value;
        private CellStyle body;
        private CellStyle center;
        private CellStyle empty;
        private CellStyle note;
    }
}
