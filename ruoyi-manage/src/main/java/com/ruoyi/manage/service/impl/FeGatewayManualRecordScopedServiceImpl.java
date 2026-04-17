package com.ruoyi.manage.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeExternalCompany;
import com.ruoyi.manage.domain.FeGatewayManualRecord;
import com.ruoyi.manage.service.IFeGatewayManualRecordService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeGatewayManualRecordScopedServiceImpl implements IFeGatewayManualRecordService
{
    @Autowired
    private FeGatewayManualRecordServiceImpl target;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeGatewayManualRecord selectFeGatewayManualRecordByRecordId(Long recordId)
    {
        FeGatewayManualRecord record = target.selectFeGatewayManualRecordByRecordId(recordId);
        checkDeptDataScope(record == null ? null : record.getDeptId());
        return record;
    }

    @Override
    @DataScope(deptAlias = "g")
    public List<FeGatewayManualRecord> selectFeGatewayManualRecordList(FeGatewayManualRecord record)
    {
        return target.selectFeGatewayManualRecordList(record);
    }

    @Override
    public int insertFeGatewayManualRecord(FeGatewayManualRecord record)
    {
        return target.insertFeGatewayManualRecord(record);
    }

    @Override
    public int updateFeGatewayManualRecord(FeGatewayManualRecord record)
    {
        FeGatewayManualRecord current = target.selectFeGatewayManualRecordByRecordId(record.getRecordId());
        checkDeptDataScope(current == null ? null : current.getDeptId());
        return target.updateFeGatewayManualRecord(record);
    }

    @Override
    public int voidFeGatewayManualRecord(Long recordId)
    {
        FeGatewayManualRecord current = target.selectFeGatewayManualRecordByRecordId(recordId);
        checkDeptDataScope(current == null ? null : current.getDeptId());
        return target.voidFeGatewayManualRecord(recordId);
    }

    @Override
    public List<FeExternalCompany> selectExternalCompanyOptions(FeExternalCompany company)
    {
        return target.selectExternalCompanyOptions(company);
    }

    @Override
    public FeExternalCompany createExternalCompany(FeExternalCompany company)
    {
        return target.createExternalCompany(company);
    }

    private void checkDeptDataScope(Long deptId)
    {
        if (!SecurityUtils.isAdmin())
        {
            if (deptId == null)
            {
                throw new ServiceException("没有权限访问该数据");
            }
            sysDeptService.checkDeptDataScope(deptId);
        }
    }
}
