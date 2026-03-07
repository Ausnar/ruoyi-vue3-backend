package com.ruoyi.record.mapper;

import java.util.List;
import com.ruoyi.record.domain.FeMaintenanceRecord;

/**
 * 灭火器维护记录Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-09
 */
public interface FeMaintenanceRecordMapper 
{
    /**
     * 查询灭火器维护记录
     * 
     * @param recordId 灭火器维护记录主键
     * @return 灭火器维护记录
     */
    public FeMaintenanceRecord selectFeMaintenanceRecordByRecordId(Long recordId);

    /**
     * 查询灭火器维护记录列表
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 灭火器维护记录集合
     */
    public List<FeMaintenanceRecord> selectFeMaintenanceRecordList(FeMaintenanceRecord feMaintenanceRecord);

    /**
     * 新增灭火器维护记录
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 结果
     */
    public int insertFeMaintenanceRecord(FeMaintenanceRecord feMaintenanceRecord);

    /**
     * 修改灭火器维护记录
     * 
     * @param feMaintenanceRecord 灭火器维护记录
     * @return 结果
     */
    public int updateFeMaintenanceRecord(FeMaintenanceRecord feMaintenanceRecord);

    /**
     * 删除灭火器维护记录
     * 
     * @param recordId 灭火器维护记录主键
     * @return 结果
     */
    public int deleteFeMaintenanceRecordByRecordId(Long recordId);

    /**
     * 批量删除灭火器维护记录
     * 
     * @param recordIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFeMaintenanceRecordByRecordIds(Long[] recordIds);
}
