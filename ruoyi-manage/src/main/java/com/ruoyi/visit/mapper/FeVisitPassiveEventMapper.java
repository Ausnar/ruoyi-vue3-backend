package com.ruoyi.visit.mapper;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitPassiveEvent;

public interface FeVisitPassiveEventMapper
{
    FeVisitPassiveEvent selectFeVisitPassiveEventByEventId(Long eventId);

    List<FeVisitPassiveEvent> selectFeVisitPassiveEventList(FeVisitPassiveEvent event);

    int countPendingByGatewayId(Long gatewayId);

    int insertFeVisitPassiveEvent(FeVisitPassiveEvent event);

    int updateFeVisitPassiveEvent(FeVisitPassiveEvent event);

    int countByVisitId(Long visitId);
}
