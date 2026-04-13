package com.ruoyi.manage.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ruoyi.manage.domain.FeExtinguisherSensorBinding;

public interface FeExtinguisherSensorBindingMapper
{
    List<FeExtinguisherSensorBinding> selectBindingsByExtinguisherId(Long extinguisherId);

    FeExtinguisherSensorBinding selectActiveBindingByExtinguisherId(Long extinguisherId);

    FeExtinguisherSensorBinding selectByExtinguisherSensorAndBindTime(@Param("extinguisherId") Long extinguisherId,
                                                                      @Param("sensorId") Long sensorId,
                                                                      @Param("bindTime") Date bindTime);

    int insertFeExtinguisherSensorBinding(FeExtinguisherSensorBinding binding);

    int deactivateActiveBinding(@Param("extinguisherId") Long extinguisherId,
                                @Param("unbindTime") Date unbindTime,
                                @Param("updateBy") String updateBy);
}
