package com.ruoyi.manage.report;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.manage.domain.report.UnitDeviceReportPreview;

@Component
public class UnitDeviceReportPdfExporter
{
    private static final String FONT_RESOURCE = "fonts/NotoSansSC-VF.ttf";

    public void export(HttpServletResponse response, UnitDeviceReportPreview preview)
    {
        String fileName = safeName(preview.getUnitInfo().getDeptName()) + "-单位设备报告.pdf";
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response.getOutputStream(), preview);
        }
        catch (IOException | RuntimeException e)
        {
            throw new ServiceException("生成单位设备报告PDF失败：" + e.getMessage());
        }
    }

    void write(OutputStream output, UnitDeviceReportPreview preview) throws IOException
    {
        try (PDDocument document = new PDDocument(); InputStream fontStream = new ClassPathResource(FONT_RESOURCE).getInputStream())
        {
            PDType0Font font = PDType0Font.load(document, fontStream, true);
            PdfLayout layout = new PdfLayout(document, font);
            layout.write(preview);
            layout.finish();
            document.save(output);
        }
    }

    private String safeName(String value)
    {
        return UnitDeviceReportFormat.text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static class PdfLayout
    {
        private static final PDRectangle PAGE_SIZE = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        private static final float MARGIN = 28f;
        private static final float BOTTOM = 30f;
        private static final float BODY_FONT_SIZE = 7.2f;
        private static final float HEADER_FONT_SIZE = 7.4f;
        private static final float LINE_HEIGHT = 9f;
        private static final Color NAVY = new Color(31, 55, 82);
        private static final Color BLUE = new Color(47, 111, 179);
        private static final Color LIGHT_BLUE = new Color(221, 235, 247);
        private static final Color BORDER = new Color(190, 200, 211);

        private final PDDocument document;
        private final PDType0Font font;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;
        private String pendingSection;

        PdfLayout(PDDocument document, PDType0Font font)
        {
            this.document = document;
            this.font = font;
        }

        void write(UnitDeviceReportPreview preview) throws IOException
        {
            newPage();
            title(preview.getReportTitle());
            meta("统计范围：" + preview.getScopeName(), "生成时间：" + preview.getGeneratedTime());

            section("一、单位基本信息");
            UnitDeviceReportPreview.UnitInfo unit = preview.getUnitInfo();
            table(new String[] { "项目", "内容", "项目", "内容" }, Arrays.asList(
                    new String[] { "单位名称", text(unit.getDeptName()), "上级单位", text(unit.getParentDeptName()) },
                    new String[] { "归属地区", UnitDeviceReportFormat.area(unit.getProvince(), unit.getCity(), unit.getArea()), "单位状态", UnitDeviceReportFormat.unitStatus(unit.getStatus()) },
                    new String[] { "负责人", text(unit.getLeader()), "联系电话", text(unit.getPhone()) },
                    new String[] { "电子邮箱", text(unit.getEmail()), "统计口径", "所选单位及下级单位" }
            ), new float[] { 0.13f, 0.37f, 0.13f, 0.37f });

            section("二、设备概况");
            UnitDeviceReportPreview.Overview o = preview.getOverview();
            table(new String[] { "下级单位", "消防点", "网关", "传感器", "灭火器", "未绑消防点网关", "未绑网关传感器", "未绑传感器灭火器" },
                    Collections.singletonList(new String[] { text(o.getChildDeptCount()), text(o.getFirePointCount()), text(o.getGatewayCount()),
                            text(o.getSensorCount()), text(o.getExtinguisherCount()), text(o.getGatewayUnboundFirePointCount()),
                            text(o.getSensorUnboundGatewayCount()), text(o.getExtinguisherUnboundSensorCount()) }),
                    new float[] { 1, 1, 1, 1, 1, 1.4f, 1.4f, 1.6f });

            section("三、消防点信息");
            List<String[]> firePoints = new ArrayList<>();
            int index = 1;
            for (UnitDeviceReportPreview.FirePointRow row : preview.getFirePoints())
            {
                firePoints.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                        text(row.getFirePointCode()), UnitDeviceReportFormat.firePointType(row.getStationType()), text(row.getLocation()),
                        join(row.getBuilding(), row.getFloor()), text(row.getExpectedExtinguisherCount()),
                        text(row.getActualExtinguisherCount()), text(row.getActualSensorCount()),
                        UnitDeviceReportFormat.firePointStatus(row.getStatus()) });
            }
            table(new String[] { "序号", "所属单位", "消防点", "编号", "类型", "位置", "建筑/楼层", "应配", "实配", "传感器", "状态" },
                    firePoints, new float[] { .45f, 1.45f, 1.3f, 1.05f, .75f, 1.2f, .9f, .55f, .55f, .65f, .55f });

            section("四、网关信息");
            List<String[]> gateways = new ArrayList<>();
            index = 1;
            for (UnitDeviceReportPreview.GatewayRow row : preview.getGateways())
            {
                gateways.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                        text(row.getExternalTboxId()), text(row.getImei()), text(row.getSim()),
                        UnitDeviceReportFormat.gatewayStatus(row.getStatus()), UnitDeviceReportFormat.dateTime(row.getLastOnlineTime()),
                        UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
            }
            table(new String[] { "序号", "所属单位", "消防点", "TBoxID", "IMEI", "SIM", "状态", "最后在线", "最后同步" },
                    gateways, new float[] { .45f, 1.55f, 1.25f, .75f, 1.25f, 1.1f, .6f, 1.25f, 1.25f });

            section("五、传感器信息");
            List<String[]> sensors = new ArrayList<>();
            index = 1;
            for (UnitDeviceReportPreview.SensorRow row : preview.getSensors())
            {
                sensors.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                        text(row.getSensorCode()), text(row.getGatewayCode()), text(row.getPressure()),
                        text(row.getTemperature()), text(row.getBatteryLevel()), UnitDeviceReportFormat.sensorStatus(row.getStatus()),
                        UnitDeviceReportFormat.dateTime(row.getLastOnlineTime()), UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
            }
            table(new String[] { "序号", "所属单位", "消防点", "传感器", "网关", "压力", "温度", "电量", "状态", "最后在线", "最后同步" },
                    sensors, new float[] { .4f, 1.35f, 1.05f, 1.05f, 1.2f, .55f, .55f, .5f, .5f, 1.15f, 1.15f });

            section("六、灭火器信息");
            List<String[]> extinguishers = new ArrayList<>();
            index = 1;
            for (UnitDeviceReportPreview.ExtinguisherRow row : preview.getExtinguishers())
            {
                extinguishers.add(new String[] { text(index++), text(row.getDeptName()), text(row.getFirePointName()),
                        text(row.getLabelCode()), join(row.getProductName(), row.getSpecification()),
                        join(UnitDeviceReportFormat.extinguisherType(row.getExtinguisherType()),
                                UnitDeviceReportFormat.extinguisherForm(row.getExtinguisherForm())),
                        text(row.getSensorCode()), UnitDeviceReportFormat.date(row.getProductionDate()),
                        UnitDeviceReportFormat.date(row.getExpiryDate()), UnitDeviceReportFormat.extinguisherStatus(row.getStatus()),
                        UnitDeviceReportFormat.dateTime(row.getLastSyncTime()) });
            }
            table(new String[] { "序号", "所属单位", "消防点", "标志铭码", "产品/规格", "类型/形式", "传感器", "生产日期", "到期日期", "状态", "最后同步" },
                    extinguishers, new float[] { .4f, 1.25f, 1f, 1.05f, 1.15f, .8f, 1.05f, .75f, .75f, .5f, 1.15f });

            section("七、数据口径说明");
            paragraph("本报告按生成时点的当前有效主数据统计。网关最后在线时间当前不一定来自SDK真实在线时间，仅作资料展示；缺失所属单位的数据不进入普通用户授权范围统计。");
        }

        void finish() throws IOException
        {
            closeStream();
            int total = document.getNumberOfPages();
            for (int i = 0; i < total; i++)
            {
                PDPage current = document.getPage(i);
                try (PDPageContentStream footer = new PDPageContentStream(document, current,
                        PDPageContentStream.AppendMode.APPEND, true, true))
                {
                    String value = "第 " + (i + 1) + " / " + total + " 页";
                    float width = textWidth(value, 7f);
                    footer.beginText();
                    footer.setFont(font, 7f);
                    footer.setNonStrokingColor(new Color(105, 115, 128));
                    footer.newLineAtOffset((PAGE_SIZE.getWidth() - width) / 2f, 15f);
                    footer.showText(value);
                    footer.endText();
                }
            }
        }

        private void title(String value) throws IOException
        {
            drawText(value, MARGIN, y, 18f, NAVY);
            y -= 30f;
        }

        private void meta(String left, String right) throws IOException
        {
            drawText(left, MARGIN, y, 8.5f, new Color(80, 92, 108));
            float rightWidth = textWidth(right, 8.5f);
            drawText(right, PAGE_SIZE.getWidth() - MARGIN - rightWidth, y, 8.5f, new Color(80, 92, 108));
            y -= 20f;
        }

        private void section(String value) throws IOException
        {
            ensureSpace(26f);
            drawSection(value);
            pendingSection = value;
        }

        private void drawSection(String value) throws IOException
        {
            float width = PAGE_SIZE.getWidth() - MARGIN * 2;
            stream.setNonStrokingColor(BLUE);
            stream.addRect(MARGIN, y - 19f, width, 22f);
            stream.fill();
            drawText(value, MARGIN + 7f, y - 12f, 10f, Color.WHITE);
            y -= 29f;
        }

        private void paragraph(String value) throws IOException
        {
            float width = PAGE_SIZE.getWidth() - MARGIN * 2 - 12f;
            List<String> lines = wrap(value, width, 8.5f);
            float height = Math.max(28f, lines.size() * 11f + 12f);
            if (y - height < BOTTOM)
            {
                newPage();
                if (pendingSection != null) drawSection(pendingSection);
            }
            pendingSection = null;
            stream.setNonStrokingColor(LIGHT_BLUE);
            stream.addRect(MARGIN, y - height, PAGE_SIZE.getWidth() - MARGIN * 2, height);
            stream.fill();
            float textY = y - 13f;
            for (String line : lines)
            {
                drawText(line, MARGIN + 6f, textY, 8.5f, NAVY);
                textY -= 11f;
            }
            y -= height + 8f;
        }

        private void table(String[] headers, List<String[]> rows, float[] weights) throws IOException
        {
            float[] widths = widths(weights);
            float firstRowHeight = rows == null || rows.isEmpty()
                    ? 20f : rowHeight(rows.get(0), widths, BODY_FONT_SIZE);
            if (y - 23f - firstRowHeight < BOTTOM)
            {
                newPage();
                if (pendingSection != null) drawSection(pendingSection);
            }
            drawTableRow(headers, widths, true);
            pendingSection = null;
            if (rows == null || rows.isEmpty())
            {
                drawTableRow(new String[] { "暂无数据" }, new float[] { PAGE_SIZE.getWidth() - MARGIN * 2 }, false);
                y -= 6f;
                return;
            }
            for (String[] row : rows)
            {
                float rowHeight = rowHeight(row, widths, BODY_FONT_SIZE);
                if (y - rowHeight < BOTTOM)
                {
                    newPage();
                    drawTableRow(headers, widths, true);
                }
                drawTableRow(row, widths, false);
            }
            y -= 8f;
        }

        private void drawTableRow(String[] values, float[] widths, boolean header) throws IOException
        {
            float fontSize = header ? HEADER_FONT_SIZE : BODY_FONT_SIZE;
            float rowHeight = header ? 23f : rowHeight(values, widths, fontSize);
            if (y - rowHeight < BOTTOM)
            {
                newPage();
            }
            float x = MARGIN;
            for (int i = 0; i < values.length; i++)
            {
                float width = widths[Math.min(i, widths.length - 1)];
                stream.setNonStrokingColor(header ? NAVY : Color.WHITE);
                stream.addRect(x, y - rowHeight, width, rowHeight);
                stream.fill();
                stream.setStrokingColor(BORDER);
                stream.addRect(x, y - rowHeight, width, rowHeight);
                stream.stroke();
                List<String> lines = wrap(text(values[i]), width - 6f, fontSize);
                float lineY = y - 11f;
                for (String line : lines)
                {
                    drawText(line, x + 3f, lineY, fontSize, header ? Color.WHITE : new Color(45, 55, 68));
                    lineY -= LINE_HEIGHT;
                }
                x += width;
            }
            y -= rowHeight;
        }

        private float rowHeight(String[] values, float[] widths, float fontSize) throws IOException
        {
            int maxLines = 1;
            for (int i = 0; i < values.length; i++)
            {
                float width = widths[Math.min(i, widths.length - 1)];
                maxLines = Math.max(maxLines, wrap(text(values[i]), width - 6f, fontSize).size());
            }
            return Math.max(20f, maxLines * LINE_HEIGHT + 8f);
        }

        private float[] widths(float[] weights)
        {
            float total = 0f;
            for (float weight : weights) total += weight;
            float available = PAGE_SIZE.getWidth() - MARGIN * 2;
            float[] result = new float[weights.length];
            for (int i = 0; i < weights.length; i++) result[i] = available * weights[i] / total;
            return result;
        }

        private List<String> wrap(String value, float maxWidth, float fontSize) throws IOException
        {
            if (value == null || value.isEmpty()) return Collections.singletonList("-");
            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (int offset = 0; offset < value.length(); )
            {
                int codePoint = value.codePointAt(offset);
                String character = new String(Character.toChars(codePoint));
                String candidate = line + character;
                if (line.length() > 0 && textWidth(candidate, fontSize) > maxWidth)
                {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                line.append(character);
                offset += Character.charCount(codePoint);
            }
            if (line.length() > 0) lines.add(line.toString());
            return lines.isEmpty() ? Collections.singletonList("-") : lines;
        }

        private void ensureSpace(float height) throws IOException
        {
            if (y - height < BOTTOM) newPage();
        }

        private void newPage() throws IOException
        {
            closeStream();
            page = new PDPage(PAGE_SIZE);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = PAGE_SIZE.getHeight() - MARGIN;
        }

        private void closeStream() throws IOException
        {
            if (stream != null)
            {
                stream.close();
                stream = null;
            }
        }

        private void drawText(String value, float x, float baseline, float fontSize, Color color) throws IOException
        {
            stream.beginText();
            stream.setFont(font, fontSize);
            stream.setNonStrokingColor(color);
            stream.newLineAtOffset(x, baseline);
            stream.showText(text(value));
            stream.endText();
        }

        private float textWidth(String value, float fontSize) throws IOException
        {
            return font.getStringWidth(text(value)) / 1000f * fontSize;
        }

        private String join(String first, String second)
        {
            String a = text(first);
            String b = text(second);
            if ("-".equals(a)) return b;
            if ("-".equals(b)) return a;
            return a + " / " + b;
        }

        private String text(Object value)
        {
            return UnitDeviceReportFormat.text(value);
        }
    }
}
