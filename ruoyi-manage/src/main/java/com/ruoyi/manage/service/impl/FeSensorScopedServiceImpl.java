package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeSensorService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeSensorScopedServiceImpl implements IFeSensorService
{
    @Autowired
    private FeSensorMapper feSensorMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeSensor selectFeSensorBySensorId(Long sensorId)
    {
        FeSensor sensor = feSensorMapper.selectFeSensorBySensorId(sensorId);
        checkDeptDataScope(sensor == null ? null : sensor.getDeptId());
        return sensor;
    }

    @Override
    @DataScope(deptAlias = "s")
    public List<FeSensor> selectFeSensorList(FeSensor feSensor)
    {
        return feSensorMapper.selectFeSensorList(feSensor);
    }

    @Override
    public int insertFeSensor(FeSensor feSensor)
    {
        validateTargetDept(feSensor.getDeptId());
        feSensor.setCreateTime(DateUtils.getNowDate());
        return feSensorMapper.insertFeSensor(feSensor);
    }

    @Override
    public int updateFeSensor(FeSensor feSensor)
    {
        selectFeSensorBySensorId(feSensor.getSensorId());
        validateTargetDept(feSensor.getDeptId());
        feSensor.setUpdateTime(DateUtils.getNowDate());
        return feSensorMapper.updateFeSensor(feSensor);
    }

    @Override
    public int deleteFeSensorBySensorIds(Long[] sensorIds)
    {
        for (Long sensorId : sensorIds)
        {
            selectFeSensorBySensorId(sensorId);
        }
        return feSensorMapper.deleteFeSensorBySensorIds(sensorIds);
    }

    @Override
    public int deleteFeSensorBySensorId(Long sensorId)
    {
        selectFeSensorBySensorId(sensorId);
        return feSensorMapper.deleteFeSensorBySensorId(sensorId);
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
