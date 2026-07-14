package com.ruoyi.manage.report;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.domain.report.RuntimeDetailRow;

@Component
public class RuntimeDetailPdfExporter
{
    private static final String FONT_RESOURCE = "fonts/NotoSansSC-VF.ttf";

    public void export(HttpServletResponse response, RuntimeDetailQuery query, List<RuntimeDetailRow> rows)
    {
        String typeName = "gateway".equals(query.getDetailType()) ? "网关定位明细" : "传感器运行明细";
        String fileName = safeName(query.getScopeName()) + "-" + safeName(query.getPeriodLabel()) + "-" + typeName + ".pdf";
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response, query, rows, typeName);
        }
        catch (IOException | RuntimeException e)
        {
            throw new ServiceException("生成设备运行明细PDF失败：" + e.getMessage());
        }
    }

    private void write(HttpServletResponse response, RuntimeDetailQuery query,
            List<RuntimeDetailRow> rows, String typeName) throws IOException
    {
        try (PDDocument document = new PDDocument();
                InputStream fontStream = new ClassPathResource(FONT_RESOURCE).getInputStream())
        {
            PDType0Font font = PDType0Font.load(document, fontStream, true);
            PdfTable table = new PdfTable(document, font, query, typeName);
            table.write(rows);
            table.finish();
            document.save(response.getOutputStream());
        }
    }

    private String safeName(String value)
    {
        return UnitDeviceReportFormat.text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    private static class PdfTable
    {
        private static final PDRectangle PAGE_SIZE = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());
        private static final float MARGIN = 24f;
        private static final float BOTTOM = 28f;
        private static final float HEADER_HEIGHT = 24f;
        private static final float ROW_HEIGHT = 19f;
        private static final Color NAVY = new Color(31, 55, 82);
        private static final Color BORDER = new Color(196, 204, 214);
        private static final Color BODY = new Color(45, 55, 68);

        private final PDDocument document;
        private final PDType0Font font;
        private final RuntimeDetailQuery query;
        private final String typeName;
        private final String[] headers;
        private final float[] widths;
        private PDPageContentStream stream;
        private float y;

        PdfTable(PDDocument document, PDType0Font font, RuntimeDetailQuery query, String typeName)
        {
            this.document = document;
            this.font = font;
            this.query = query;
            this.typeName = typeName;
            this.headers = headers(query.getDetailType());
            this.widths = widths(query.getDetailType());
        }

        void write(List<RuntimeDetailRow> rows) throws IOException
        {
            newPage();
            for (RuntimeDetailRow row : rows)
            {
                if (y - ROW_HEIGHT < BOTTOM) newPage();
                drawRow(values(query.getDetailType(), row), false);
            }
            if (rows.isEmpty())
            {
                drawText("暂无数据", MARGIN + 8f, y - 14f, 8f, BODY);
                y -= ROW_HEIGHT;
            }
        }

        void finish() throws IOException
        {
            closeStream();
            int total = document.getNumberOfPages();
            for (int i = 0; i < total; i++)
            {
                PDPage page = document.getPage(i);
                try (PDPageContentStream footer = new PDPageContentStream(document, page,
                        PDPageContentStream.AppendMode.APPEND, true, true))
                {
                    String value = "第 " + (i + 1) + " / " + total + " 页";
                    float width = font.getStringWidth(value) / 1000f * 7f;
                    footer.beginText();
                    footer.setFont(font, 7f);
                    footer.setNonStrokingColor(new Color(105, 115, 128));
                    footer.newLineAtOffset((PAGE_SIZE.getWidth() - width) / 2f, 14f);
                    footer.showText(value);
                    footer.endText();
                }
            }
        }

        private void newPage() throws IOException
        {
            closeStream();
            PDPage page = new PDPage(PAGE_SIZE);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = PAGE_SIZE.getHeight() - MARGIN;
            drawText(query.getScopeName() + typeName, MARGIN, y, 15f, NAVY);
            y -= 23f;
            drawText("统计周期：" + query.getPeriodLabel(), MARGIN, y, 8f, new Color(90, 100, 112));
            y -= 18f;
            drawRow(headers, true);
        }

        private void closeStream() throws IOException
        {
            if (stream != null)
            {
                stream.close();
                stream = null;
            }
        }

        private void drawRow(String[] values, boolean header) throws IOException
        {
            float height = header ? HEADER_HEIGHT : ROW_HEIGHT;
            float x = MARGIN;
            for (int i = 0; i < values.length; i++)
            {
                float width = widths[i];
                stream.setNonStrokingColor(header ? NAVY : Color.WHITE);
                stream.addRect(x, y - height, width, height);
                stream.fill();
                stream.setStrokingColor(BORDER);
                stream.addRect(x, y - height, width, height);
                stream.stroke();
                String text = fit(values[i], width - 6f, header ? 7.1f : 6.8f);
                drawText(text, x + 3f, y - (header ? 15f : 13f), header ? 7.1f : 6.8f,
                        header ? Color.WHITE : BODY);
                x += width;
            }
            y -= height;
        }

        private void drawText(String value, float x, float baseline, float size, Color color) throws IOException
        {
            stream.beginText();
            stream.setFont(font, size);
            stream.setNonStrokingColor(color);
            stream.newLineAtOffset(x, baseline);
            stream.showText(text(value));
            stream.endText();
        }

        private String fit(String value, float maxWidth, float size) throws IOException
        {
            String source = text(value);
            if (font.getStringWidth(source) / 1000f * size <= maxWidth) return source;
            String suffix = "...";
            StringBuilder result = new StringBuilder();
            for (int offset = 0; offset < source.length(); )
            {
                int codePoint = source.codePointAt(offset);
                String next = result.toString() + new String(Character.toChars(codePoint)) + suffix;
                if (font.getStringWidth(next) / 1000f * size > maxWidth) break;
                result.appendCodePoint(codePoint);
                offset += Character.charCount(codePoint);
            }
            return result.append(suffix).toString();
        }

        private String[] headers(String detailType)
        {
            if ("gateway".equals(detailType))
            {
                return new String[] { "GPS时间", "同步时间", "所属单位", "消防点", "TBoxID", "网关IMEI", "经度", "纬度" };
            }
            return new String[] { "采集时间", "所属单位", "消防点", "传感器编号", "网关编号", "压力", "温度", "电量", "信号", "状态", "质量" };
        }

        private float[] widths(String detailType)
        {
            if ("gateway".equals(detailType))
            {
                return scaled(new float[] { 1.25f, 1.25f, 1.6f, 1.2f, .9f, 1.35f, 1f, 1f });
            }
            return scaled(new float[] { 1.25f, 1.45f, 1.1f, 1.15f, 1.25f, .65f, .55f, .5f, .65f, .55f, .65f });
        }

        private float[] scaled(float[] weights)
        {
            float total = 0f;
            for (float value : weights) total += value;
            float available = PAGE_SIZE.getWidth() - MARGIN * 2;
            float[] result = new float[weights.length];
            for (int i = 0; i < weights.length; i++) result[i] = available * weights[i] / total;
            return result;
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
    }
}
