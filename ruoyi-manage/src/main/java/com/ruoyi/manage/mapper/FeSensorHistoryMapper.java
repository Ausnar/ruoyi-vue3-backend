package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.FeSensorHistory;
import org.apache.ibatis.annotations.Param;

/**
 * 传感器历史数据Mapper接口
 *
 * @author ruoyi
 * @date 2026-03-09
 */
public interface FeSensorHistoryMapper
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
    public List<FeSensorHistory> selectFeSensorHistoryBySensorId(
            @Param("sensorId") Long sensorId,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime);

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
