package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeSensorHistory;

/**
 * oyi.manage传感器历史数据Service接口
 *
 * @author ruoyi
 * @date 2026-03-09
 */
public interface IFeSensorHistoryService
{
    /**
     * 查询传感器历史数据
     */
    public FeSensorHistory selectFeSensorHistoryByHistoryId(Long historyId);

    /**
     * 查询传感器历史数据列表
     */
    public List<FeSensorHistory> selectFeSensorHistoryList(FeSensorHistory feSensorHistory);

    /**
     * 根据传感器ID查询历史数据（用于图表）
     */
    public List<FeSensorHistory> selectFeSensorHistoryBySensorId(Long sensorId, String startTime, String endTime);

    /**
     * 新增传感器历史数据
     */
    public int insertFeSensorHistory(FeSensorHistory feSensorHistory);

    /**
     * 删除传感器历史数据
     */
    public int deleteFeSensorHistoryByHistoryId(Long historyId);

    /**
     * 批量删除传感器历史数据
     */
    public int deleteFeSensorHistoryByHistoryIds(Long[] historyIds);
}
