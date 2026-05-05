package com.ruoyi.manage.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeDeviceWarning;
import com.ruoyi.manage.mapper.FeDeviceWarningMapper;
import com.ruoyi.manage.service.IFeDeviceWarningService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class FeDeviceWarningServiceImpl implements IFeDeviceWarningService
{
    @Autowired
    private FeDeviceWarningMapper feDeviceWarningMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeDeviceWarning selectFeDeviceWarningByWarningId(Long warningId)
    {
        FeDeviceWarning warning = feDeviceWarningMapper.selectFeDeviceWarningByWarningId(warningId);
        checkDeptDataScope(warning == null ? null : warning.getDeptId());
        return warning;
    }

    @Override
    @DataScope(deptAlias = "w")
    public List<FeDeviceWarning> selectFeDeviceWarningList(FeDeviceWarning warning)
    {
        return feDeviceWarningMapper.selectFeDeviceWarningList(warning);
    }

    @Override
    @DataScope(deptAlias = "w")
    public Map<String, Object> selectDashboardOverview(FeDeviceWarning warning)
    {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pendingCount", feDeviceWarningMapper.countOpenWarnings(warning));
        data.put("severeCount", feDeviceWarningMapper.countOpenSevereWarnings(warning));
        data.put("recentWarnings", feDeviceWarningMapper.selectRecentDashboardWarnings(warning));
        return data;
    }

    @Override
    public FeDeviceWarning selectOpenWarningByObject(String warningType, String objectType, Long objectId)
    {
        return feDeviceWarningMapper.selectOpenWarningByObject(warningType, objectType, objectId);
    }

    @Override
    public int insertFeDeviceWarning(FeDeviceWarning warning)
    {
        if (StringUtils.isBlank(warning.getWarningStatus()))
        {
            warning.setWarningStatus(STATUS_PENDING);
        }
        warning.setCreateTime(DateUtils.getNowDate());
        return feDeviceWarningMapper.insertFeDeviceWarning(warning);
    }

    @Override
    public int updateFeDeviceWarning(FeDeviceWarning warning)
    {
        warning.setUpdateTime(DateUtils.getNowDate());
        return feDeviceWarningMapper.updateFeDeviceWarning(warning);
    }

    @Override
    @Transactional
    public FeDeviceWarning saveOrRefreshOpenWarning(FeDeviceWarning warning, String operator)
    {
        Date now = DateUtils.getNowDate();
        FeDeviceWarning existed = feDeviceWarningMapper.selectOpenWarningByObject(
            warning.getWarningType(), warning.getObjectType(), warning.getObjectId());
        if (existed == null)
        {
            if (warning.getTriggerTime() == null)
            {
                warning.setTriggerTime(warning.getLastTriggerTime() == null ? now : warning.getLastTriggerTime());
            }
            if (warning.getLastTriggerTime() == null)
            {
                warning.setLastTriggerTime(warning.getTriggerTime());
            }
            warning.setWarningStatus(StringUtils.defaultIfBlank(warning.getWarningStatus(), STATUS_PENDING));
            warning.setCreateBy(operator);
            warning.setCreateTime(now);
            warning.setUpdateBy(operator);
            warning.setUpdateTime(now);
            feDeviceWarningMapper.insertFeDeviceWarning(warning);
            return warning;
        }

        existed.setDeptId(warning.getDeptId());
        existed.setSourceDeptId(warning.getSourceDeptId());
        existed.setFirePointId(warning.getFirePointId());
        existed.setGatewayId(warning.getGatewayId());
        existed.setSensorId(warning.getSensorId());
        existed.setExtinguisherId(warning.getExtinguisherId());
        existed.setLastTriggerTime(warning.getLastTriggerTime() == null ? now : warning.getLastTriggerTime());
        existed.setWindowStartTime(warning.getWindowStartTime());
        existed.setWindowEndTime(warning.getWindowEndTime());
        existed.setSampleCount(warning.getSampleCount());
        existed.setThresholdSnapshot(warning.getThresholdSnapshot());
        existed.setEvidenceSummary(warning.getEvidenceSummary());
        existed.setRemark(warning.getRemark());
        existed.setUpdateBy(operator);
        existed.setUpdateTime(now);
        feDeviceWarningMapper.updateFeDeviceWarning(existed);
        return existed;
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
