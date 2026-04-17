package com.ruoyi.manage.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeCompanyDeptMapping;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.mapper.FeCompanyDeptMappingMapper;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.mapper.FeExternalCompanyMapper;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeCompanyDeptMappingService;

@Service
public class FeCompanyDeptMappingServiceImpl implements IFeCompanyDeptMappingService
{
    private static final String STATUS_ACTIVE = "active";

    @Autowired
    private FeCompanyDeptMappingMapper feCompanyDeptMappingMapper;

    @Autowired
    private FeExternalCompanyMapper feExternalCompanyMapper;

    @Autowired
    private FeFirePointMapper feFirePointMapper;

    @Autowired
    private FeGatewayMapper feGatewayMapper;

    @Autowired
    private FeSensorMapper feSensorMapper;

    @Autowired
    private FeExtinguisherMapper feExtinguisherMapper;

    @Override
    public FeCompanyDeptMapping selectFeCompanyDeptMappingByMappingId(Long mappingId)
    {
        return feCompanyDeptMappingMapper.selectFeCompanyDeptMappingByMappingId(mappingId);
    }

    @Override
    public List<FeCompanyDeptMapping> selectFeCompanyDeptMappingList(FeCompanyDeptMapping mapping)
    {
        if (!SecurityUtils.isAdmin())
        {
            mapping.setDeptId(SecurityUtils.getDeptId());
        }
        return feCompanyDeptMappingMapper.selectFeCompanyDeptMappingList(mapping);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeCompanyDeptMapping(FeCompanyDeptMapping mapping)
    {
        fillExternalCompanySnapshot(mapping);
        validateUniqueExternalCompany(mapping);
        mapping.setSyncStatus(defaultSyncStatus(mapping.getSyncStatus()));
        mapping.setCreateBy(SecurityUtils.getUsername());
        mapping.setCreateTime(DateUtils.getNowDate());
        mapping.setUpdateBy(SecurityUtils.getUsername());
        mapping.setUpdateTime(DateUtils.getNowDate());
        int rows = feCompanyDeptMappingMapper.insertFeCompanyDeptMapping(mapping);
        syncRelatedDept(mapping.getExternalCompanyId(), mapping.getDeptId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeCompanyDeptMapping(FeCompanyDeptMapping mapping)
    {
        fillExternalCompanySnapshot(mapping);
        validateUniqueExternalCompany(mapping);
        mapping.setSyncStatus(defaultSyncStatus(mapping.getSyncStatus()));
        mapping.setUpdateBy(SecurityUtils.getUsername());
        mapping.setUpdateTime(DateUtils.getNowDate());
        int rows = feCompanyDeptMappingMapper.updateFeCompanyDeptMapping(mapping);
        syncRelatedDept(mapping.getExternalCompanyId(), mapping.getDeptId());
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFeCompanyDeptMappingByMappingIds(Long[] mappingIds)
    {
        List<Long> externalCompanyIds = new ArrayList<Long>();
        for (Long mappingId : mappingIds)
        {
            FeCompanyDeptMapping mapping = feCompanyDeptMappingMapper.selectFeCompanyDeptMappingByMappingId(mappingId);
            if (mapping != null && mapping.getExternalCompanyId() != null)
            {
                externalCompanyIds.add(mapping.getExternalCompanyId());
            }
        }
        int rows = feCompanyDeptMappingMapper.deleteFeCompanyDeptMappingByMappingIds(mappingIds);
        for (Long externalCompanyId : externalCompanyIds)
        {
            syncRelatedDept(externalCompanyId, null);
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFeCompanyDeptMappingByMappingId(Long mappingId)
    {
        FeCompanyDeptMapping mapping = feCompanyDeptMappingMapper.selectFeCompanyDeptMappingByMappingId(mappingId);
        int rows = feCompanyDeptMappingMapper.deleteFeCompanyDeptMappingByMappingId(mappingId);
        if (mapping != null && mapping.getExternalCompanyId() != null)
        {
            syncRelatedDept(mapping.getExternalCompanyId(), null);
        }
        return rows;
    }

    @Override
    public List<FeExternalCompany> selectExternalCompanyOptions(FeExternalCompany company)
    {
        if (company == null)
        {
            company = new FeExternalCompany();
        }
        company.setRecordStatus(STATUS_ACTIVE);
        if (!SecurityUtils.isAdmin())
        {
            company.setLastSourceDeptId(SecurityUtils.getDeptId());
        }
        return feExternalCompanyMapper.selectFeExternalCompanyList(company);
    }

    private void fillExternalCompanySnapshot(FeCompanyDeptMapping mapping)
    {
        if (mapping.getExternalCompanyId() == null)
        {
            throw new ServiceException("请选择外部单位");
        }
        FeExternalCompany externalCompany = feExternalCompanyMapper.selectByExternalCompanyId(mapping.getExternalCompanyId());
        if (externalCompany == null)
        {
            throw new ServiceException("未找到对应的外部单位，请先完成外部单位治理");
        }
        if (!STATUS_ACTIVE.equals(externalCompany.getRecordStatus()))
        {
            throw new ServiceException("当前外部单位不是生效状态，请重新选择");
        }
        mapping.setExternalCompanyName(externalCompany.getExternalCompanyName());
    }

    private void validateUniqueExternalCompany(FeCompanyDeptMapping mapping)
    {
        FeCompanyDeptMapping exists = feCompanyDeptMappingMapper.selectByExternalCompanyId(mapping.getExternalCompanyId());
        if (exists != null && (mapping.getMappingId() == null || !exists.getMappingId().equals(mapping.getMappingId())))
        {
            throw new ServiceException("该外部单位已存在映射，请直接修改");
        }
    }

    private String defaultSyncStatus(String syncStatus)
    {
        return (syncStatus == null || syncStatus.trim().isEmpty()) ? STATUS_ACTIVE : syncStatus;
    }

    private void syncRelatedDept(Long externalCompanyId, Long deptId)
    {
        if (externalCompanyId == null)
        {
            return;
        }
        feFirePointMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
        feGatewayMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
        feSensorMapper.updateDeptIdByGatewayExternalCompanyId(externalCompanyId, deptId);
        feExtinguisherMapper.updateDeptIdByExternalCompanyId(externalCompanyId, deptId);
    }
}
