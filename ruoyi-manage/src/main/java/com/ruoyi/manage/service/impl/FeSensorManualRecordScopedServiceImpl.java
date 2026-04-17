package com.ruoyi.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeSensorManualRecord;
import com.ruoyi.manage.service.IFeSensorManualRecordService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeSensorManualRecordScopedServiceImpl implements IFeSensorManualRecordService
{
    @Autowired
    private FeSensorManualRecordServiceImpl target;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeSensorManualRecord selectFeSensorManualRecordBySensorRecordId(Long sensorRecordId)
    {
        FeSensorManualRecord record = target.selectFeSensorManualRecordBySensorRecordId(sensorRecordId);
        checkDeptDataScope(record == null ? null : record.getDeptId());
        return record;
    }

    @Override
    @DataScope(deptAlias = "s")
    public List<FeSensorManualRecord> selectFeSensorManualRecordList(FeSensorManualRecord record)
    {
        return target.selectFeSensorManualRecordList(record);
    }

    @Override
    public int insertFeSensorManualRecord(FeSensorManualRecord record)
    {
        return target.insertFeSensorManualRecord(record);
    }

    @Override
    public int updateFeSensorManualRecord(FeSensorManualRecord record)
    {
        FeSensorManualRecord current = target.selectFeSensorManualRecordBySensorRecordId(record.getSensorRecordId());
        checkDeptDataScope(current == null ? null : current.getDeptId());
        return target.updateFeSensorManualRecord(record);
    }

    @Override
    public int voidFeSensorManualRecord(Long sensorRecordId)
    {
        FeSensorManualRecord current = target.selectFeSensorManualRecordBySensorRecordId(sensorRecordId);
        checkDeptDataScope(current == null ? null : current.getDeptId());
        return target.voidFeSensorManualRecord(sensorRecordId);
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
