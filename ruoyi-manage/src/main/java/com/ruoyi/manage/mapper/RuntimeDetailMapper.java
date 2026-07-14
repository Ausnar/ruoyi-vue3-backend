package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.report.RuntimeDetailFirePointOption;
import com.ruoyi.manage.domain.report.RuntimeDetailDeviceOption;
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.domain.report.RuntimeDetailRow;

public interface RuntimeDetailMapper
{
    List<RuntimeDetailRow> selectRuntimeDetailList(RuntimeDetailQuery query);

    List<RuntimeDetailRow> selectRuntimeDetailExportBatch(RuntimeDetailQuery query);

    long countRuntimeDetails(RuntimeDetailQuery query);

    List<RuntimeDetailFirePointOption> selectFirePointOptions(RuntimeDetailQuery query);

    List<RuntimeDetailDeviceOption> selectDeviceOptions(RuntimeDetailQuery query);
}
