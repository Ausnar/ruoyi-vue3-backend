package com.ruoyi.record.mapper;

import java.util.List;
import com.ruoyi.record.domain.FeAlarmRecord;

/**
 * 报警记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
public interface FeAlarmRecordMapper 
{
    /**
     * 查询报警记录
     * 
     * @param alarmId 报警记录主键
     * @return 报警记录
     */
    public FeAlarmRecord selectFeAlarmRecordByAlarmId(Long alarmId);

    /**
     * 查询报警记录列表
     * 
     * @param feAlarmRecord 报警记录
     * @return 报警记录集合
     */
    public List<FeAlarmRecord> selectFeAlarmRecordList(FeAlarmRecord feAlarmRecord);

    /**
     * 新增报警记录
     * 
     * @param feAlarmRecord 报警记录
     * @return 结果
     */
    public int insertFeAlarmRecord(FeAlarmRecord feAlarmRecord);

    /**
     * 修改报警记录
     * 
     * @param feAlarmRecord 报警记录
     * @return 结果
     */
    public int updateFeAlarmRecord(FeAlarmRecord feAlarmRecord);

    /**
     * 删除报警记录
     * 
     * @param alarmId 报警记录主键
     * @return 结果
     */
    public int deleteFeAlarmRecordByAlarmId(Long alarmId);

    /**
     * 批量删除报警记录
     * 
     * @param alarmIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFeAlarmRecordByAlarmIds(Long[] alarmIds);
}
