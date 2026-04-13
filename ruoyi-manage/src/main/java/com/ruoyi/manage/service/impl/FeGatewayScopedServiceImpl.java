package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.service.IFeGatewayService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeGatewayScopedServiceImpl implements IFeGatewayService
{
    @Autowired
    private FeGatewayMapper feGatewayMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeGateway selectFeGatewayByGatewayId(Long gatewayId)
    {
        FeGateway gateway = feGatewayMapper.selectFeGatewayByGatewayId(gatewayId);
        checkDeptDataScope(gateway == null ? null : gateway.getDeptId());
        return gateway;
    }

    @Override
    @DataScope(deptAlias = "g")
    public List<FeGateway> selectFeGatewayList(FeGateway feGateway)
    {
        return feGatewayMapper.selectFeGatewayList(feGateway);
    }

    private void checkDeptDataScope(Long deptId)
    {
        if (!SecurityUtils.isAdmin())
        {
            if (deptId == null)
            {
                throw new ServiceException("没有权限访问该数据");
            }
            sysDeptService.checkDeptDataScope(deptId);
        }
    }
}
