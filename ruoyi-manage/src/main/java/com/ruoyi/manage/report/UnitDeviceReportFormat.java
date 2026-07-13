package com.ruoyi.manage.report;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

final class UnitDeviceReportFormat
{
    private static final ZoneId ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private UnitDeviceReportFormat()
    {
    }

    static String text(Object value)
    {
        if (value == null)
        {
            return "-";
        }
        String text = String.valueOf(value);
        return text.trim().isEmpty() ? "-" : text;
    }

    static String date(Date value)
    {
        return value == null ? "-" : DATE.format(value.toInstant().atZone(ZONE));
    }

    static String dateTime(Date value)
    {
        return value == null ? "-" : DATE_TIME.format(value.toInstant().atZone(ZONE));
    }

    static String area(String province, String city, String area)
    {
        StringBuilder value = new StringBuilder();
        append(value, province);
        append(value, city);
        append(value, area);
        return value.length() == 0 ? "-" : value.toString();
    }

    static String unitStatus(String status)
    {
        return "0".equals(status) ? "正常" : "1".equals(status) ? "停用" : text(status);
    }

    static String firePointStatus(String status)
    {
        return "0".equals(status) ? "正常" : "1".equals(status) ? "停用" : text(status);
    }

    static String firePointType(String type)
    {
        if ("D".equalsIgnoreCase(type)) return "楼用 / 建筑物";
        if ("B".equalsIgnoreCase(type)) return "车用 / 公交车辆";
        return text(type);
    }

    static String sensorStatus(String status)
    {
        if ("0".equals(status)) return "正常";
        if ("1".equals(status)) return "异常";
        if ("2".equals(status)) return "离线";
        return text(status);
    }

    static String gatewayStatus(String status)
    {
        if ("0".equals(status) || "online".equalsIgnoreCase(text(status))) return "在线";
        if ("1".equals(status) || "abnormal".equalsIgnoreCase(text(status))) return "异常";
        if ("2".equals(status) || "offline".equalsIgnoreCase(text(status))) return "离线";
        return text(status);
    }

    static String extinguisherStatus(String status)
    {
        if ("0".equals(status)) return "正常";
        if ("1".equals(status)) return "待检";
        if ("2".equals(status)) return "过期";
        if ("3".equals(status)) return "停用";
        if ("4".equals(status)) return "报废";
        return text(status);
    }

    static String extinguisherType(String type)
    {
        if ("water_based".equals(type)) return "水基型";
        if ("dry_powder".equals(type)) return "干粉";
        if ("clean_gas".equals(type)) return "洁净气体";
        if ("co2".equals(type)) return "二氧化碳";
        return text(type);
    }

    static String extinguisherForm(String form)
    {
        if ("portable".equals(form)) return "手提式";
        if ("wheeled".equals(form)) return "推车式";
        return text(form);
    }

    private static void append(StringBuilder value, String part)
    {
        if (part == null || part.trim().isEmpty()) return;
        if (value.length() > 0) value.append(" / ");
        value.append(part);
    }
}
