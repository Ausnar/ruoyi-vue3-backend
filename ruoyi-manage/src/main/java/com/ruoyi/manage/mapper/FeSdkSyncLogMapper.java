package com.ruoyi.manage.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeSdkSyncLog;

public interface FeSdkSyncLogMapper
{
    int insertFeSdkSyncLog(FeSdkSyncLog log);

    int updateFeSdkSyncLog(FeSdkSyncLog log);

    int closeAbandonedRunningLogs(@Param("cutoffTime") Date cutoffTime,
                                  @Param("closeTime") Date closeTime,
                                  @Param("operator") String operator,
                                  @Param("message") String message);
}
