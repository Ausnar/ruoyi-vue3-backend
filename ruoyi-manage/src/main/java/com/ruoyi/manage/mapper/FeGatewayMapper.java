package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeGateway;

public interface FeGatewayMapper
{
    FeGateway selectFeGatewayByGatewayId(Long gatewayId);

    FeGateway selectByExternalTboxId(Long externalTboxId);

    FeGateway selectByImei(String imei);

    List<FeGateway> selectFeGatewayList(FeGateway feGateway);

    int insertFeGateway(FeGateway feGateway);

    int updateFeGateway(FeGateway feGateway);

    int updateDeptIdByExternalCompanyId(@Param("externalCompanyId") Long externalCompanyId, @Param("deptId") Long deptId);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId);
}
