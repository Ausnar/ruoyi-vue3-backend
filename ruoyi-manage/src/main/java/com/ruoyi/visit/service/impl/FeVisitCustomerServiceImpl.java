package com.ruoyi.visit.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.visit.domain.FeVisitCustomer;
import com.ruoyi.visit.mapper.FeVisitCustomerMapper;
import com.ruoyi.visit.service.IFeVisitCustomerService;

@Service
public class FeVisitCustomerServiceImpl implements IFeVisitCustomerService
{
    @Autowired
    private FeVisitCustomerMapper feVisitCustomerMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeVisitCustomer selectFeVisitCustomerByCustomerId(Long customerId)
    {
        FeVisitCustomer customer = feVisitCustomerMapper.selectFeVisitCustomerByCustomerId(customerId);
        if (customer == null)
        {
            return null;
        }
        checkCustomerScope(customer);
        return customer;
    }

    @Override
    public List<FeVisitCustomer> selectFeVisitCustomerList(FeVisitCustomer customer)
    {
        if (!SecurityUtils.isAdmin())
        {
            customer.setScopeDeptIds(sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId()));
        }
        return feVisitCustomerMapper.selectFeVisitCustomerList(customer);
    }

    @Override
    public int insertFeVisitCustomer(FeVisitCustomer customer)
    {
        customer.setDeptId(SecurityUtils.getDeptId());
        customer.setCreateBy(SecurityUtils.getUsername());
        customer.setCreateTime(DateUtils.getNowDate());
        customer.setUpdateBy(SecurityUtils.getUsername());
        customer.setUpdateTime(DateUtils.getNowDate());
        customer.setDelFlag("0");
        return feVisitCustomerMapper.insertFeVisitCustomer(customer);
    }

    @Override
    public int updateFeVisitCustomer(FeVisitCustomer customer)
    {
        FeVisitCustomer origin = selectFeVisitCustomerByCustomerId(customer.getCustomerId());
        if (origin == null)
        {
            throw new ServiceException("未找到对应的独立客户档案");
        }
        customer.setDeptId(origin.getDeptId());
        customer.setUpdateBy(SecurityUtils.getUsername());
        customer.setUpdateTime(DateUtils.getNowDate());
        return feVisitCustomerMapper.updateFeVisitCustomer(customer);
    }

    @Override
    public int deleteFeVisitCustomerByCustomerIds(Long[] customerIds)
    {
        for (Long customerId : customerIds)
        {
            FeVisitCustomer customer = feVisitCustomerMapper.selectFeVisitCustomerByCustomerId(customerId);
            if (customer == null)
            {
                continue;
            }
            checkCustomerScope(customer);
        }
        return feVisitCustomerMapper.deleteFeVisitCustomerByCustomerIds(customerIds);
    }

    private void checkCustomerScope(FeVisitCustomer customer)
    {
        if (SecurityUtils.isAdmin())
        {
            return;
        }
        List<Long> scopeDeptIds = sysDeptService.selectDeptAndChildrenIds(SecurityUtils.getDeptId());
        if (customer.getDeptId() == null || !scopeDeptIds.contains(customer.getDeptId()))
        {
            throw new ServiceException("无权访问该独立客户档案");
        }
    }
}
