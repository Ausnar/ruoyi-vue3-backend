package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.service.IFeGatewayService;

@Service
public class FeGatewayServiceImpl implements IFeGatewayService
{
    @Autowired
    private FeGatewayMapper feGatewayMapper;

    @Override
    public FeGateway selectFeGatewayByGatewayId(Long gatewayId)
    {
        return feGatewayMapper.selectFeGatewayByGatewayId(gatewayId);
    }

    @Override
    public List<FeGateway> selectFeGatewayList(FeGateway feGateway)
    {
        if (!SecurityUtils.isAdmin())
        {
            feGateway.setDeptId(SecurityUtils.getDeptId());
        }
        return feGatewayMapper.selectFeGatewayList(feGateway);
    }
}
