package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeGatewayManualRecord;

public interface FeGatewayManualRecordMapper
{
    FeGatewayManualRecord selectFeGatewayManualRecordByRecordId(Long recordId);

    List<FeGatewayManualRecord> selectFeGatewayManualRecordList(FeGatewayManualRecord record);

    int insertFeGatewayManualRecord(FeGatewayManualRecord record);

    int updateFeGatewayManualRecord(FeGatewayManualRecord record);

    int updateExternalCompanyRef(@Param("sourceExternalCompanyId") Long sourceExternalCompanyId,
        @Param("targetExternalCompanyId") Long targetExternalCompanyId,
        @Param("targetExternalCompanyNameSnapshot") String targetExternalCompanyNameSnapshot);
}
