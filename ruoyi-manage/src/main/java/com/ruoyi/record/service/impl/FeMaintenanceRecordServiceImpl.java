package com.ruoyi.record.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.record.mapper.FeMaintenanceRecordMapper;
import com.ruoyi.record.domain.FeMaintenanceRecord;
import com.ruoyi.record.service.IFeMaintenanceRecordService;

/**
 * 灭火器维护记录Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
@Service
public class FeMaintenanceRecordServiceImpl implements IFeMaintenanceRecordService 
{
    @Autowired
    private FeMaintenanceRecordMapper feMaintenanceRecordMapper;

    /**
     * 查询灭火器维护记录
     * 
     * @param recordId 灭火器维护记录主键
     * @return 灭火器维护记录
     */
    @Override
    public FeMaintenanceRecord selectFeMaintenanceRecordByRecordId(Long recordId)
    {
        return feMaintenanceRecordMapper.selectFeMaintenanceRecordByRecordId(recordId);
    }

    /**
     * 查询灭火器维护记录列表
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 灭火器维护记录
     */
    @Override
    public List<FeMaintenanceRecord> selectFeMaintenanceRecordList(FeMaintenanceRecord feMaintenanceRecord)
    {
        return feMaintenanceRecordMapper.selectFeMaintenanceRecordList(feMaintenanceRecord);
    }

    /**
     * 新增灭火器维护记录
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 结果
     */
    @Override
    public int insertFeMaintenanceRecord(FeMaintenanceRecord feMaintenanceRecord)
    {
        feMaintenanceRecord.setCreateTime(DateUtils.getNowDate());
        return feMaintenanceRecordMapper.insertFeMaintenanceRecord(feMaintenanceRecord);
    }

    /**
     * 修改灭火器维护记录
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 结果
     */
    @Override
    public int updateFeMaintenanceRecord(FeMaintenanceRecord feMaintenanceRecord)
    {
        feMaintenanceRecord.setUpdateTime(DateUtils.getNowDate());
        return feMaintenanceRecordMapper.updateFeMaintenanceRecord(feMaintenanceRecord);
    }

    /**
     * 批量删除灭火器维护记录
     * 
     * @param recordIds 需要删除的灭火器维护记录主键
     * @return 结果
     */
    @Override
    public int deleteFeMaintenanceRecordByRecordIds(Long[] recordIds)
    {
        return feMaintenanceRecordMapper.deleteFeMaintenanceRecordByRecordIds(recordIds);
    }

    /**
     * 删除灭火器维护记录信息
     * 
     * @param recordId 灭火器维护记录主键
     * @return 结果
     */
    @Override
    public int deleteFeMaintenanceRecordByRecordId(Long recordId)
    {
        return feMaintenanceRecordMapper.deleteFeMaintenanceRecordByRecordId(recordId);
    }
}
