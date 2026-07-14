package com.ruoyi.manage.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.ruoyi.manage.domain.report.RuntimeDetailFirePointOption;
import com.ruoyi.manage.domain.report.RuntimeDetailDeviceOption;
import com.ruoyi.manage.domain.report.RuntimeDetailQuery;
import com.ruoyi.manage.domain.report.RuntimeDetailRow;

public interface IRuntimeDetailService
{
    List<RuntimeDetailRow> selectRuntimeDetailList(RuntimeDetailQuery query);

    List<RuntimeDetailFirePointOption> selectFirePointOptions(RuntimeDetailQuery query);

    List<RuntimeDetailDeviceOption> selectDeviceOptions(RuntimeDetailQuery query);

    void exportExcel(HttpServletResponse response, RuntimeDetailQuery query);

    void exportPdf(HttpServletResponse response, RuntimeDetailQuery query);
}
