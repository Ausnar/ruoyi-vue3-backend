package com.ruoyi.record.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.record.mapper.FeAlarmRecordMapper;
import com.ruoyi.record.domain.FeAlarmRecord;
import com.ruoyi.record.service.IFeAlarmRecordService;

/**
 * 报警记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
@Service
public class FeAlarmRecordServiceImpl implements IFeAlarmRecordService 
{
    @Autowired
    private FeAlarmRecordMapper feAlarmRecordMapper;

    /**
     * 查询报警记录
     * 
     * @param alarmId 报警记录主键
     * @return 报警记录
     */
    @Override
    public FeAlarmRecord selectFeAlarmRecordByAlarmId(Long alarmId)
    {
        return feAlarmRecordMapper.selectFeAlarmRecordByAlarmId(alarmId);
    }

    /**
     * 查询报警记录列表
     * 
     * @param feAlarmRecord 报警记录
     * @return 报警记录
     */
    @Override
    public List<FeAlarmRecord> selectFeAlarmRecordList(FeAlarmRecord feAlarmRecord)
    {
        return feAlarmRecordMapper.selectFeAlarmRecordList(feAlarmRecord);
    }

    /**
     * 新增报警记录
     * 
     * @param feAlarmRecord 报警记录
     * @return 结果
     */
    @Override
    public int insertFeAlarmRecord(FeAlarmRecord feAlarmRecord)
    {
        feAlarmRecord.setCreateTime(DateUtils.getNowDate());
        return feAlarmRecordMapper.insertFeAlarmRecord(feAlarmRecord);
    }

    /**
     * 修改报警记录
     * 
     * @param feAlarmRecord 报警记录
     * @return 结果
     */
    @Override
    public int updateFeAlarmRecord(FeAlarmRecord feAlarmRecord)
    {
        return feAlarmRecordMapper.updateFeAlarmRecord(feAlarmRecord);
    }

    /**
     * 批量删除报警记录
     * 
     * @param alarmIds 需要删除的报警记录主键
     * @return 结果
     */
    @Override
    public int deleteFeAlarmRecordByAlarmIds(Long[] alarmIds)
    {
        return feAlarmRecordMapper.deleteFeAlarmRecordByAlarmIds(alarmIds);
    }

    /**
     * 删除报警记录信息
     * 
     * @param alarmId 报警记录主键
     * @return 结果
     */
    @Override
    public int deleteFeAlarmRecordByAlarmId(Long alarmId)
    {
        return feAlarmRecordMapper.deleteFeAlarmRecordByAlarmId(alarmId);
    }
}
