package com.ruoyi.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.FeExtinguisher;
import com.ruoyi.manage.domain.FeGatewayManualRecord;
import com.ruoyi.manage.domain.FeSensor;
import com.ruoyi.manage.domain.FeSensorManualRecord;
import com.ruoyi.manage.mapper.FeExtinguisherMapper;
import com.ruoyi.manage.mapper.FeGatewayManualRecordMapper;
import com.ruoyi.manage.mapper.FeSensorManualRecordMapper;
import com.ruoyi.manage.mapper.FeSensorMapper;
import com.ruoyi.manage.service.IFeSensorManualRecordService;

@Service
public class FeSensorManualRecordServiceImpl implements IFeSensorManualRecordService
{
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_VOID = "void";
    private static final String MATCH_UNMATCHED = "unmatched";
    private static final String MATCH_PARTIAL = "partial_matched";
    private static final String MATCH_FULL = "full_matched";

    @Autowired
    private FeSensorManualRecordMapper sensorManualRecordMapper;

    @Autowired
    private FeGatewayManualRecordMapper gatewayManualRecordMapper;

    @Autowired
    private FeSensorMapper sensorMapper;

    @Autowired
    private FeExtinguisherMapper extinguisherMapper;

    @Override
    public FeSensorManualRecord selectFeSensorManualRecordBySensorRecordId(Long sensorRecordId)
    {
        return sensorManualRecordMapper.selectFeSensorManualRecordBySensorRecordId(sensorRecordId);
    }

    @Override
    public List<FeSensorManualRecord> selectFeSensorManualRecordList(FeSensorManualRecord record)
    {
        return sensorManualRecordMapper.selectFeSensorManualRecordList(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeSensorManualRecord(FeSensorManualRecord record)
    {
        FeGatewayManualRecord gatewayRecord = requireGatewayRecord(record.getGatewayRecordId());
        inheritGatewayScope(record, gatewayRecord);
        refreshMatchFields(record);
        record.setStatus(defaultValue(record.getStatus(), STATUS_ACTIVE));
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(DateUtils.getNowDate());
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return sensorManualRecordMapper.insertFeSensorManualRecord(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeSensorManualRecord(FeSensorManualRecord record)
    {
        FeSensorManualRecord current = sensorManualRecordMapper.selectFeSensorManualRecordBySensorRecordId(record.getSensorRecordId());
        if (current == null)
        {
            throw new ServiceException("未找到对应的传感器施工记录");
        }
        FeGatewayManualRecord gatewayRecord = requireGatewayRecord(record.getGatewayRecordId() != null ? record.getGatewayRecordId() : current.getGatewayRecordId());
        record.setGatewayRecordId(gatewayRecord.getRecordId());
        inheritGatewayScope(record, gatewayRecord);
        refreshMatchFields(record);
        if (StringUtils.isBlank(record.getStatus()))
        {
            record.setStatus(current.getStatus());
        }
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return sensorManualRecordMapper.updateFeSensorManualRecord(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int voidFeSensorManualRecord(Long sensorRecordId)
    {
        FeSensorManualRecord record = sensorManualRecordMapper.selectFeSensorManualRecordBySensorRecordId(sensorRecordId);
        if (record == null)
        {
            throw new ServiceException("未找到对应的传感器施工记录");
        }
        record.setStatus(STATUS_VOID);
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return sensorManualRecordMapper.updateFeSensorManualRecord(record);
    }

    private FeGatewayManualRecord requireGatewayRecord(Long gatewayRecordId)
    {
        if (gatewayRecordId == null)
        {
            throw new ServiceException("请选择所属网关记录");
        }
        FeGatewayManualRecord gatewayRecord = gatewayManualRecordMapper.selectFeGatewayManualRecordByRecordId(gatewayRecordId);
        if (gatewayRecord == null)
        {
            throw new ServiceException("未找到所属网关记录");
        }
        return gatewayRecord;
    }

    private void inheritGatewayScope(FeSensorManualRecord record, FeGatewayManualRecord gatewayRecord)
    {
        record.setExternalCompanyId(gatewayRecord.getExternalCompanyId());
        record.setExternalCompanyNameSnapshot(gatewayRecord.getExternalCompanyNameSnapshot());
        record.setDeptId(gatewayRecord.getDeptId());
    }

    private void refreshMatchFields(FeSensorManualRecord record)
    {
        FeSensor sensor = null;
        if (StringUtils.isNotBlank(record.getSensorCode()))
        {
            sensor = sensorMapper.selectBySensorCode(record.getSensorCode().trim());
        }
        if (sensor == null && StringUtils.isNotBlank(record.getMacAddress()))
        {
            sensor = sensorMapper.selectByMac(record.getMacAddress().trim());
        }
        FeExtinguisher extinguisher = null;
        if (StringUtils.isNotBlank(record.getExtinguisherBodySerialNo()))
        {
            extinguisher = extinguisherMapper.selectByLabelCode(record.getExtinguisherBodySerialNo().trim());
        }
        record.setSensorId(sensor == null ? null : sensor.getSensorId());
        record.setExtinguisherId(extinguisher == null ? null : extinguisher.getExtinguisherId());
        record.setMatchStatus(resolveMatchStatus(record, sensor, extinguisher));
    }

    private String resolveMatchStatus(FeSensorManualRecord record, FeSensor sensor, FeExtinguisher extinguisher)
    {
        boolean sensorMatched = sensor != null;
        boolean extinguisherRequired = StringUtils.isNotBlank(record.getExtinguisherBodySerialNo());
        boolean extinguisherMatched = extinguisher != null;
        if (sensorMatched && (!extinguisherRequired || extinguisherMatched))
        {
            return MATCH_FULL;
        }
        if (sensorMatched || extinguisherMatched)
        {
            return MATCH_PARTIAL;
        }
        return MATCH_UNMATCHED;
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
