package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeFirePoint;

public interface FeFirePointMapper
{
    FeFirePoint selectFeFirePointByFirePointId(Long firePointId);

    List<FeFirePoint> selectFeFirePointList(FeFirePoint feFirePoint);

    FeFirePoint selectByExternalStationId(Long externalStationId);

    FeFirePoint selectByStationNumber(String stationNumber);

    List<FeFirePoint> selectByExternalCompanyId(Long externalCompanyId);

    List<FeFirePoint> selectBySourceDeptIdAndExternalCompanyId(Long sourceDeptId, Long externalCompanyId);

    int insertFeFirePoint(FeFirePoint feFirePoint);

    int updateFeFirePoint(FeFirePoint feFirePoint);

    int updateDeptIdByExternalCompanyId(@Param("externalCompanyId") Long externalCompanyId, @Param("deptId") Long deptId);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId);

    int deleteFeFirePointByFirePointId(Long firePointId);

    int deleteFeFirePointByFirePointIds(Long[] firePointIds);
}
