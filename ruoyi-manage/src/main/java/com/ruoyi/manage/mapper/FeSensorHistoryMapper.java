package com.ruoyi.manage.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeSensorHistory;

public interface FeSensorHistoryMapper
{
    public FeSensorHistory selectFeSensorHistoryByHistoryId(Long historyId);

    public List<FeSensorHistory> selectFeSensorHistoryList(FeSensorHistory feSensorHistory);

    public List<FeSensorHistory> selectFeSensorHistoryBySensorId(@Param("sensorId") Long sensorId,
                                                                 @Param("startTime") String startTime,
                                                                 @Param("endTime") String endTime);

    public int countBySensorIdAndCreateTime(@Param("sensorId") Long sensorId, @Param("createTime") java.util.Date createTime);

    public int insertFeSensorHistory(FeSensorHistory feSensorHistory);

    public int deleteFeSensorHistoryByHistoryId(Long historyId);

    public int deleteFeSensorHistoryByHistoryIds(Long[] historyIds);
}
