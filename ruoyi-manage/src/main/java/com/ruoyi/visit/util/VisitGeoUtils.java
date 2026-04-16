package com.ruoyi.visit.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class VisitGeoUtils
{
    private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);
    private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final double EARTH_RADIUS_M = 6371000D;

    private VisitGeoUtils()
    {
    }

    public static boolean isValidGps(BigDecimal longitude, BigDecimal latitude)
    {
        if (longitude == null || latitude == null)
        {
            return false;
        }
        if (BigDecimal.ZERO.compareTo(longitude) == 0 && BigDecimal.ZERO.compareTo(latitude) == 0)
        {
            return false;
        }
        return longitude.compareTo(MIN_LONGITUDE) >= 0
            && longitude.compareTo(MAX_LONGITUDE) <= 0
            && latitude.compareTo(MIN_LATITUDE) >= 0
            && latitude.compareTo(MAX_LATITUDE) <= 0;
    }

    public static BigDecimal calculateDistanceMeters(BigDecimal fromLongitude, BigDecimal fromLatitude,
        BigDecimal toLongitude, BigDecimal toLatitude)
    {
        if (!isValidGps(fromLongitude, fromLatitude) || !isValidGps(toLongitude, toLatitude))
        {
            return null;
        }
        double fromLat = Math.toRadians(fromLatitude.doubleValue());
        double fromLng = Math.toRadians(fromLongitude.doubleValue());
        double toLat = Math.toRadians(toLatitude.doubleValue());
        double toLng = Math.toRadians(toLongitude.doubleValue());
        double deltaLat = toLat - fromLat;
        double deltaLng = toLng - fromLng;
        double haversine = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
            + Math.cos(fromLat) * Math.cos(toLat) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double distance = 2 * EARTH_RADIUS_M * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }
}
