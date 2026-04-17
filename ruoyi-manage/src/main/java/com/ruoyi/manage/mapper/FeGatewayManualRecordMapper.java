package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.FeGatewayManualRecord;

public interface FeGatewayManualRecordMapper
{
    FeGatewayManualRecord selectFeGatewayManualRecordByRecordId(Long recordId);

    List<FeGatewayManualRecord> selectFeGatewayManualRecordList(FeGatewayManualRecord record);

    int insertFeGatewayManualRecord(FeGatewayManualRecord record);

    int updateFeGatewayManualRecord(FeGatewayManualRecord record);
}
