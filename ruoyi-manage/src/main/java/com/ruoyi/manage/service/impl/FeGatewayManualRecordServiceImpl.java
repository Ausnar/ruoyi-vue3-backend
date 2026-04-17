package com.ruoyi.manage.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeGateway;
import com.ruoyi.manage.domain.FeGatewayManualRecord;
import com.ruoyi.manage.mapper.FeExternalCompanyMapper;
import com.ruoyi.manage.mapper.FeGatewayManualRecordMapper;
import com.ruoyi.manage.mapper.FeGatewayMapper;
import com.ruoyi.manage.mapper.FeSensorManualRecordMapper;
import com.ruoyi.manage.service.IFeGatewayManualRecordService;

@Service
public class FeGatewayManualRecordServiceImpl implements IFeGatewayManualRecordService
{
    private static final String STATUS_ACTIVE = "active";
    private static final String STATUS_VOID = "void";
    private static final String MATCHED = "matched";
    private static final String UNMATCHED = "unmatched";
    private static final String SOURCE_MANUAL = "manual";
    private static final String YES = "1";
    private static final String NO = "0";

    @Autowired
    private FeGatewayManualRecordMapper gatewayManualRecordMapper;

    @Autowired
    private FeSensorManualRecordMapper sensorManualRecordMapper;

    @Autowired
    private FeGatewayMapper gatewayMapper;

    @Autowired
    private FeExternalCompanyMapper externalCompanyMapper;

    @Override
    public FeGatewayManualRecord selectFeGatewayManualRecordByRecordId(Long recordId)
    {
        FeGatewayManualRecord record = gatewayManualRecordMapper.selectFeGatewayManualRecordByRecordId(recordId);
        if (record != null)
        {
            record.setSensorRecords(sensorManualRecordMapper.selectByGatewayRecordId(recordId));
        }
        return record;
    }

    @Override
    public List<FeGatewayManualRecord> selectFeGatewayManualRecordList(FeGatewayManualRecord record)
    {
        return gatewayManualRecordMapper.selectFeGatewayManualRecordList(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeGatewayManualRecord(FeGatewayManualRecord record)
    {
        fillExternalCompanySnapshot(record);
        fillDefaultDept(record);
        refreshMatchFields(record);
        record.setStatus(defaultValue(record.getStatus(), STATUS_ACTIVE));
        record.setCreateBy(SecurityUtils.getUsername());
        record.setCreateTime(DateUtils.getNowDate());
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return gatewayManualRecordMapper.insertFeGatewayManualRecord(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeGatewayManualRecord(FeGatewayManualRecord record)
    {
        FeGatewayManualRecord current = gatewayManualRecordMapper.selectFeGatewayManualRecordByRecordId(record.getRecordId());
        if (current == null)
        {
            throw new ServiceException("未找到对应的网关施工记录");
        }
        fillExternalCompanySnapshot(record);
        preserveDept(record, current);
        refreshMatchFields(record);
        if (StringUtils.isBlank(record.getStatus()))
        {
            record.setStatus(current.getStatus());
        }
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return gatewayManualRecordMapper.updateFeGatewayManualRecord(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int voidFeGatewayManualRecord(Long recordId)
    {
        FeGatewayManualRecord record = gatewayManualRecordMapper.selectFeGatewayManualRecordByRecordId(recordId);
        if (record == null)
        {
            throw new ServiceException("未找到对应的网关施工记录");
        }
        record.setStatus(STATUS_VOID);
        record.setUpdateBy(SecurityUtils.getUsername());
        record.setUpdateTime(DateUtils.getNowDate());
        return gatewayManualRecordMapper.updateFeGatewayManualRecord(record);
    }

    @Override
    public List<FeExternalCompany> selectExternalCompanyOptions(FeExternalCompany company)
    {
        if (company == null)
        {
            company = new FeExternalCompany();
        }
        company.setRecordStatus(STATUS_ACTIVE);
        return externalCompanyMapper.selectFeExternalCompanyList(company);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FeExternalCompany createExternalCompany(FeExternalCompany company)
    {
        if (company == null || StringUtils.isBlank(company.getExternalCompanyName()))
        {
            throw new ServiceException("外部单位名称不能为空");
        }
        String externalCompanyName = company.getExternalCompanyName().trim();
        FeExternalCompany query = new FeExternalCompany();
        query.setExternalCompanyName(externalCompanyName);
        query.setRecordStatus(STATUS_ACTIVE);
        List<FeExternalCompany> exists = externalCompanyMapper.selectFeExternalCompanyList(query);
        if (exists != null)
        {
            for (FeExternalCompany item : exists)
            {
                if (externalCompanyName.equals(item.getExternalCompanyName()))
                {
                    return item;
                }
            }
        }
        Long minId = externalCompanyMapper.selectMinExternalCompanyId();
        long nextManualId = (minId != null && minId.longValue() < 0L) ? (minId.longValue() - 1L) : -1L;
        Date now = DateUtils.getNowDate();
        company.setExternalCompanyId(nextManualId);
        company.setExternalCompanyName(externalCompanyName);
        company.setSyncStatus(defaultValue(company.getSyncStatus(), SOURCE_MANUAL));
        company.setFirstSourceType(defaultValue(company.getFirstSourceType(), SOURCE_MANUAL));
        company.setSdkObserved(defaultValue(company.getSdkObserved(), NO));
        company.setManualCreated(defaultValue(company.getManualCreated(), YES));
        company.setManualCreatedBy(SecurityUtils.getUsername());
        company.setManualCreatedTime(now);
        company.setRecordStatus(defaultValue(company.getRecordStatus(), STATUS_ACTIVE));
        company.setFirstSeenTime(now);
        company.setLastSeenTime(now);
        company.setLastSourceDeptId(SecurityUtils.getDeptId());
        company.setCreateBy(SecurityUtils.getUsername());
        company.setCreateTime(now);
        company.setUpdateBy(SecurityUtils.getUsername());
        company.setUpdateTime(now);
        externalCompanyMapper.insertFeExternalCompany(company);
        return externalCompanyMapper.selectByExternalCompanyId(company.getExternalCompanyId());
    }

    private void fillExternalCompanySnapshot(FeGatewayManualRecord record)
    {
        if (record.getExternalCompanyId() == null)
        {
            record.setExternalCompanyNameSnapshot(null);
            return;
        }
        FeExternalCompany externalCompany = externalCompanyMapper.selectByExternalCompanyId(record.getExternalCompanyId());
        if (externalCompany == null)
        {
            throw new ServiceException("未找到对应的外部单位，请先创建后再保存");
        }
        if (!STATUS_ACTIVE.equals(externalCompany.getRecordStatus()))
        {
            throw new ServiceException("当前外部单位不是生效状态，请重新选择");
        }
        record.setExternalCompanyNameSnapshot(externalCompany.getExternalCompanyName());
    }

    private void fillDefaultDept(FeGatewayManualRecord record)
    {
        Long deptId = SecurityUtils.getDeptId();
        if (record.getDeptId() == null)
        {
            record.setDeptId(deptId);
        }
        if (record.getSourceDeptId() == null)
        {
            record.setSourceDeptId(deptId);
        }
    }

    private void preserveDept(FeGatewayManualRecord record, FeGatewayManualRecord current)
    {
        if (record.getDeptId() == null)
        {
            record.setDeptId(current.getDeptId());
        }
        if (record.getSourceDeptId() == null)
        {
            record.setSourceDeptId(current.getSourceDeptId());
        }
    }

    private void refreshMatchFields(FeGatewayManualRecord record)
    {
        FeGateway matchedGateway = null;
        if (StringUtils.isNotBlank(record.getGatewayImei()))
        {
            matchedGateway = gatewayMapper.selectByImei(record.getGatewayImei().trim());
        }
        if (matchedGateway != null)
        {
            record.setGatewayId(matchedGateway.getGatewayId());
            record.setMatchStatus(MATCHED);
        }
        else
        {
            record.setGatewayId(null);
            record.setMatchStatus(UNMATCHED);
        }
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
