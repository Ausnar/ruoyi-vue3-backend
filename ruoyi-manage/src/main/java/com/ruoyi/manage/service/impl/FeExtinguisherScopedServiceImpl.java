package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeExtinguisher;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.service.IFeExtinguisherService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeExtinguisherScopedServiceImpl implements IFeExtinguisherService
{
    @Autowired
    private FeExtinguisherMapper feExtinguisherMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeExtinguisher selectFeExtinguisherByExtinguisherId(Long extinguisherId)
    {
        FeExtinguisher extinguisher = feExtinguisherMapper.selectFeExtinguisherByExtinguisherId(extinguisherId);
        checkDeptDataScope(extinguisher == null ? null : extinguisher.getDeptId());
        return extinguisher;
    }

    @Override
    @DataScope(deptAlias = "e")
    public List<FeExtinguisher> selectFeExtinguisherList(FeExtinguisher feExtinguisher)
    {
        return feExtinguisherMapper.selectFeExtinguisherList(feExtinguisher);
    }

    @Override
    public int insertFeExtinguisher(FeExtinguisher feExtinguisher)
    {
        validateTargetDept(feExtinguisher.getDeptId());
        FeExtinguisherBusinessFieldUtils.completeWarningBusinessFields(feExtinguisher);
        FeExtinguisherBusinessFieldUtils.markManualProfileEdit(feExtinguisher, null);
        feExtinguisher.setCreateTime(DateUtils.getNowDate());
        return feExtinguisherMapper.insertFeExtinguisher(feExtinguisher);
    }

    @Override
    public int updateFeExtinguisher(FeExtinguisher feExtinguisher)
    {
        FeExtinguisher existing = selectFeExtinguisherByExtinguisherId(feExtinguisher.getExtinguisherId());
        validateTargetDept(feExtinguisher.getDeptId());
        FeExtinguisherBusinessFieldUtils.completeWarningBusinessFields(feExtinguisher);
        FeExtinguisherBusinessFieldUtils.markManualProfileEdit(feExtinguisher, existing == null ? null : existing.getProfileSource());
        feExtinguisher.setUpdateTime(DateUtils.getNowDate());
        return feExtinguisherMapper.updateFeExtinguisher(feExtinguisher);
    }

    @Override
    public int deleteFeExtinguisherByExtinguisherIds(Long[] extinguisherIds)
    {
        for (Long extinguisherId : extinguisherIds)
        {
            selectFeExtinguisherByExtinguisherId(extinguisherId);
        }
        return feExtinguisherMapper.deleteFeExtinguisherByExtinguisherIds(extinguisherIds);
    }

    @Override
    public int deleteFeExtinguisherByExtinguisherId(Long extinguisherId)
    {
        selectFeExtinguisherByExtinguisherId(extinguisherId);
        return feExtinguisherMapper.deleteFeExtinguisherByExtinguisherId(extinguisherId);
    }

    private void validateTargetDept(Long deptId)
    {
        if (!SecurityUtils.isAdmin() && deptId != null)
        {
            sysDeptService.checkDeptDataScope(deptId);
        }
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
