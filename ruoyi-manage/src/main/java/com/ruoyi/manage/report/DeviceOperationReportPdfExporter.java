package com.ruoyi.manage.report;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.report.DeviceOperationReportPreview;

@Component
public class DeviceOperationReportPdfExporter
{
    private static final String FONT_RESOURCE = "fonts/NotoSansSC-VF.ttf";

    public void export(HttpServletResponse response, DeviceOperationReportPreview preview)
    {
        String fileName = safeName(preview.getUnitInfo().getDeptName()) + "-设备运行" + preview.getPeriodTypeName() + ".pdf";
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        try
        {
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''"
                    + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()).replace("+", "%20"));
            write(response, preview);
        }
        catch (IOException | RuntimeException e)
        {
            throw new ServiceException("生成设备运行报告PDF失败：" + e.getMessage());
        }
    }

    private void write(HttpServletResponse response, DeviceOperationReportPreview preview) throws IOException
    {
        try (PDDocument document = new PDDocument(); InputStream fontStream = new ClassPathResource(FONT_RESOURCE).getInputStream())
        {
            PDType0Font font = PDType0Font.load(document, fontStream, true);
            Layout layout = new Layout(document, font);
            layout.write(preview);
            layout.finish();
            document.save(response.getOutputStream());
        }
    }

    private static class Layout
    {
        private static final float MARGIN = 42f;
        private static final float BOTTOM = 38f;
        private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
        private static final float CONTENT_WIDTH = PAGE_WIDTH - MARGIN * 2;
        private static final Color BLACK = Color.BLACK;
        private static final Color DARK_GREY = new Color(70, 70, 70);
        private static final Color LIGHT_GREY = new Color(235, 235, 235);
        private static final Color BORDER = new Color(175, 175, 175);

        private final PDDocument document;
        private final PDType0Font font;
        private PDPage page;
        private PDPageContentStream stream;
        private float y;

        Layout(PDDocument document, PDType0Font font)
        {
            this.document = document;
            this.font = font;
        }

        void write(DeviceOperationReportPreview preview) throws IOException
        {
            newPage();
            centered(preview.getReportTitle(), 17f, BLACK);
            y -= 14f;
            centered("统计周期：" + preview.getPeriodLabel() + "    生成时间：" + preview.getGeneratedTime(), 8.5f, DARK_GREY);
            y -= 22f;

            section("一、单位与统计周期");
            row("单位名称", text(preview.getUnitInfo().getDeptName()), "上级单位", text(preview.getUnitInfo().getParentDeptName()));
            row("归属地区", area(preview.getUnitInfo()), "统计范围", text(preview.getScopeName()));
            row("报告周期", text(preview.getPeriodLabel()), "报告类型", text(preview.getPeriodTypeName()));

            DeviceOperationReportPreview.AssetOverview assets = preview.getAssetOverview();
            section("二、设备资产概况");
            row("消防点", text(assets.getFirePointCount()), "网关", text(assets.getGatewayCount()));
            row("传感器", text(assets.getSensorCount()), "灭火器", text(assets.getExtinguisherCount()));

            DeviceOperationReportPreview.SensorSummary sensor = preview.getSensorSummary();
            section("三、传感器采样概况");
            metric("纳入传感器", sensor.getSensorCount(), "统计范围内有效传感器数量");
            metric("有采样传感器", sensor.getReportingSensorCount(), "本周期至少产生一条历史采样");
            metric("无采样传感器", sensor.getNoSampleSensorCount(), "本周期未产生历史采样");
            metric("历史采样总数", sensor.getSampleCount(), "本周期传感器历史记录数量");
            metric("有效压力样本", sensor.getValidPressureCount(), "压力值位于0至2000范围");
            metric("压力脏值", sensor.getInvalidPressureCount(), "压力小于0或大于2000，仅作数据质量统计");
            metric("压力缺失样本", sensor.getMissingPressureCount(), "历史记录存在但压力为空");
            metric("平均压力(MPa)", sensor.getAvgPressure(), "仅使用有效压力样本计算");
            metric("平均温度(℃)", sensor.getAvgTemperature(), "本周期非空温度样本平均值");
            metric("平均电量(%)", sensor.getAvgBatteryLevel(), "本周期非空电量样本平均值");

            DeviceOperationReportPreview.GatewaySummary gateway = preview.getGatewaySummary();
            section("四、网关定位数据概况");
            metric("纳入网关", gateway.getGatewayCount(), "统计范围内有效网关数量");
            metric("有GPS记录网关", gateway.getReportingGatewayCount(), "本周期至少产生一条GPS历史记录");
            metric("无GPS记录网关", gateway.getNoGpsGatewayCount(), "本周期未产生GPS历史记录");
            metric("GPS记录总数", gateway.getGpsRecordCount(), "本周期网关GPS历史记录数量");

            DeviceOperationReportPreview.ExtinguisherSummary extinguisher = preview.getExtinguisherSummary();
            section("五、灭火器资料概况");
            metric("灭火器总数", extinguisher.getExtinguisherCount(), "统计范围内有效灭火器数量");
            metric("本周期已同步", extinguisher.getSyncedCount(), "最后同步时间落在本周期");
            metric("业务资料完整", extinguisher.getProfileCompleteCount(), "生产日期、类型、形式、标准及温度范围齐全");
            metric("业务资料不完整", extinguisher.getProfileIncompleteCount(), "上述资料至少一项缺失");
            metric("已绑定传感器", extinguisher.getBoundSensorCount(), "已建立灭火器与传感器关联");
            metric("未绑定传感器", extinguisher.getUnboundSensorCount(), "尚未建立灭火器与传感器关联");

            DeviceOperationReportPreview.FirePointSummary firePoint = preview.getFirePointSummary();
            section("六、消防点快照概况");
            metric("消防点总数", firePoint.getFirePointCount(), "统计范围内有效消防点数量");
            metric("有快照消防点", firePoint.getSnapshotCoveredCount(), "本周期至少产生一条设备快照");
            metric("无快照消防点", firePoint.getNoSnapshotCount(), "本周期未产生设备快照");
            metric("设备快照总数", firePoint.getSnapshotCount(), "本周期消防点设备快照数量");
            metric("已设置应配数量", firePoint.getExpectedConfiguredCount(), "应配灭火器数量大于0的消防点");

            section("七、统计口径说明");
            paragraph("本报告仅汇总普通设备运行和数据质量信息，不在生成时临时判定低压、高压、低电量、温度异常等预警。预警生成、持续覆盖、自动恢复和状态流转由设备预警模块负责；待其历史口径稳定后，本报告再统一读取预警结果。");
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
                    footer.beginText();
                    footer.setFont(font, 7f);
                    footer.setNonStrokingColor(DARK_GREY);
                    footer.newLineAtOffset((PAGE_WIDTH - width(value, 7f)) / 2f, 18f);
                    footer.showText(value);
                    footer.endText();
                }
            }
        }

        private void section(String value) throws IOException
        {
            ensure(34f);
            y -= 12f;
            stream.setNonStrokingColor(LIGHT_GREY);
            stream.addRect(MARGIN, y - 18f, CONTENT_WIDTH, 22f);
            stream.fill();
            draw(value, MARGIN + 8f, y - 11f, 10.5f, BLACK);
            y -= 26f;
        }

        private void row(String label1, String value1, String label2, String value2) throws IOException
        {
            ensure(25f);
            float[] widths = { 90f, CONTENT_WIDTH / 2f - 90f, 90f, CONTENT_WIDTH / 2f - 90f };
            String[] values = { label1, value1, label2, value2 };
            tableRow(values, widths, new boolean[] { true, false, true, false });
        }

        private void metric(String label, Object value, String note) throws IOException
        {
            ensure(23f);
            tableRow(new String[] { label, text(value), note }, new float[] { 125f, 85f, CONTENT_WIDTH - 210f },
                    new boolean[] { true, false, false });
        }

        private void tableRow(String[] values, float[] widths, boolean[] shaded) throws IOException
        {
            float height = 21f;
            float x = MARGIN;
            for (int i = 0; i < values.length; i++)
            {
                if (shaded[i])
                {
                    stream.setNonStrokingColor(LIGHT_GREY);
                    stream.addRect(x, y - height, widths[i], height);
                    stream.fill();
                }
                stream.setStrokingColor(BORDER);
                stream.setLineWidth(.5f);
                stream.addRect(x, y - height, widths[i], height);
                stream.stroke();
                draw(fit(values[i], widths[i] - 10f, 8.3f), x + 5f, y - 14f, 8.3f, BLACK);
                x += widths[i];
            }
            y -= height;
        }

        private void paragraph(String value) throws IOException
        {
            float size = 8.5f;
            StringBuilder line = new StringBuilder();
            for (int i = 0; i < value.length(); i++)
            {
                String candidate = line.toString() + value.charAt(i);
                if (width(candidate, size) > CONTENT_WIDTH)
                {
                    ensure(14f);
                    draw(line.toString(), MARGIN, y - 10f, size, DARK_GREY);
                    y -= 14f;
                    line.setLength(0);
                }
                line.append(value.charAt(i));
            }
            if (line.length() > 0)
            {
                ensure(14f);
                draw(line.toString(), MARGIN, y - 10f, size, DARK_GREY);
                y -= 14f;
            }
        }

        private void centered(String value, float size, Color color) throws IOException
        {
            draw(value, (PAGE_WIDTH - width(value, size)) / 2f, y, size, color);
        }

        private void draw(String value, float x, float baseline, float size, Color color) throws IOException
        {
            stream.beginText();
            stream.setFont(font, size);
            stream.setNonStrokingColor(color);
            stream.newLineAtOffset(x, baseline);
            stream.showText(value);
            stream.endText();
        }

        private String fit(String value, float maxWidth, float size) throws IOException
        {
            String text = text(value);
            if (width(text, size) <= maxWidth) return text;
            String suffix = "...";
            while (text.length() > 1 && width(text + suffix, size) > maxWidth)
            {
                text = text.substring(0, text.length() - 1);
            }
            return text + suffix;
        }

        private float width(String value, float size) throws IOException
        {
            return font.getStringWidth(text(value)) / 1000f * size;
        }

        private void ensure(float height) throws IOException
        {
            if (y - height < BOTTOM) newPage();
        }

        private void newPage() throws IOException
        {
            closeStream();
            page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            stream = new PDPageContentStream(document, page);
            y = PDRectangle.A4.getHeight() - MARGIN;
        }

        private void closeStream() throws IOException
        {
            if (stream != null)
            {
                stream.close();
                stream = null;
            }
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
    }

    private static String text(Object value)
    {
        return value == null || StringUtils.isBlank(String.valueOf(value)) ? "-" : String.valueOf(value);
    }

    private String safeName(String value)
    {
        return text(value).replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
