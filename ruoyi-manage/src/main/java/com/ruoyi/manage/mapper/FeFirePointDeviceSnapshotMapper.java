package com.ruoyi.manage.mapper;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

public interface FeFirePointDeviceSnapshotMapper
{
    int insertSnapshotsForSourceDept(@Param("configId") Long configId,
                                     @Param("sourceDeptId") Long sourceDeptId,
                                     @Param("snapshotTime") Date snapshotTime,
                                     @Param("operator") String operator);
}
