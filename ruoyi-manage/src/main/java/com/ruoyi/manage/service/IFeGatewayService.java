package com.ruoyi.manage.service;

import java.util.List;

import com.ruoyi.manage.domain.FeGateway;

public interface IFeGatewayService
{
    FeGateway selectFeGatewayByGatewayId(Long gatewayId);

    List<FeGateway> selectFeGatewayList(FeGateway feGateway);
}
