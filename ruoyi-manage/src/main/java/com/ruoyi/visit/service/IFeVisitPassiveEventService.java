package com.ruoyi.visit.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.visit.domain.FeGatewayGpsHistory;
import com.ruoyi.visit.domain.FeVisitPassiveEvent;
import com.ruoyi.visit.domain.dto.VisitPassiveEventConfirmRequest;

public interface IFeVisitPassiveEventService
{
    void captureGatewayGpsAndDetectEvent(FeGateway gateway, BigDecimal gpsLongitude, BigDecimal gpsLatitude,
        Date gpsTime, Date syncTime, SysDeptApiConfig config, String operator);

    List<FeVisitPassiveEvent> selectFeVisitPassiveEventList(FeVisitPassiveEvent event);

    FeVisitPassiveEvent selectFeVisitPassiveEventByEventId(Long eventId);

    FeGatewayGpsHistory selectLatestGpsHistoryByGatewayId(Long gatewayId);

    int confirmPassiveEvent(Long eventId, VisitPassiveEventConfirmRequest request);

    int ignorePassiveEvent(Long eventId);
}
