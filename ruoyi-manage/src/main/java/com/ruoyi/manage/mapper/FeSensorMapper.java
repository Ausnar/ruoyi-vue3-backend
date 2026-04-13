package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeSensor;

public interface FeSensorMapper
{
    FeSensor selectFeSensorBySensorId(Long sensorId);

    List<FeSensor> selectFeSensorList(FeSensor feSensor);

    FeSensor selectByExternalSensorId(Long externalSensorId);

    FeSensor selectBySensorCode(String sensorCode);

    FeSensor selectByMac(String mac);

    int insertFeSensor(FeSensor feSensor);

    int updateFeSensor(FeSensor feSensor);

    int deleteFeSensorBySensorId(Long sensorId);

    int deleteFeSensorBySensorIds(Long[] sensorIds);

    int updateFeSensorByCode(FeSensor feSensor);

    int updateDeptIdByGatewayExternalCompanyId(@Param("externalCompanyId") Long externalCompanyId, @Param("deptId") Long deptId);
}
