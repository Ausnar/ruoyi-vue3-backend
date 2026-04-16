package com.ruoyi.visit.mapper;

import com.ruoyi.visit.domain.FeGatewayGpsHistory;

public interface FeGatewayGpsHistoryMapper
{
    FeGatewayGpsHistory selectLatestGpsHistoryByGatewayId(Long gatewayId);

    int insertFeGatewayGpsHistory(FeGatewayGpsHistory history);
}
