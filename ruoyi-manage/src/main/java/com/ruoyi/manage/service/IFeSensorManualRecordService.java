package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeSensorManualRecord;

public interface IFeSensorManualRecordService
{
    FeSensorManualRecord selectFeSensorManualRecordBySensorRecordId(Long sensorRecordId);

    List<FeSensorManualRecord> selectFeSensorManualRecordList(FeSensorManualRecord record);

    int insertFeSensorManualRecord(FeSensorManualRecord record);

    int updateFeSensorManualRecord(FeSensorManualRecord record);

    int voidFeSensorManualRecord(Long sensorRecordId);
}
