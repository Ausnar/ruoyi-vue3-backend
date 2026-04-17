package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeGatewayManualRecord;

public interface IFeGatewayManualRecordService
{
    FeGatewayManualRecord selectFeGatewayManualRecordByRecordId(Long recordId);

    List<FeGatewayManualRecord> selectFeGatewayManualRecordList(FeGatewayManualRecord record);

    int insertFeGatewayManualRecord(FeGatewayManualRecord record);

    int updateFeGatewayManualRecord(FeGatewayManualRecord record);

    int voidFeGatewayManualRecord(Long recordId);

    List<FeExternalCompany> selectExternalCompanyOptions(FeExternalCompany company);

    FeExternalCompany createExternalCompany(FeExternalCompany company);
}
