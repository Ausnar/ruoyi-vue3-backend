package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeSensorManualRecord;

public interface FeSensorManualRecordMapper
{
    FeSensorManualRecord selectFeSensorManualRecordBySensorRecordId(Long sensorRecordId);

    List<FeSensorManualRecord> selectFeSensorManualRecordList(FeSensorManualRecord record);

    List<FeSensorManualRecord> selectByGatewayRecordId(Long gatewayRecordId);

    int insertFeSensorManualRecord(FeSensorManualRecord record);

    int updateFeSensorManualRecord(FeSensorManualRecord record);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId,
        @Param("targetExternalCompanyNameSnapshot") String targetExternalCompanyNameSnapshot);
}
