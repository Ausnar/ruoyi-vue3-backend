package com.ruoyi.manage.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.report.UnitDeviceReportPreview;
import com.ruoyi.manage.domain.report.UnitDeviceReportQuery;
import com.ruoyi.manage.mapper.UnitDeviceReportMapper;
import com.ruoyi.manage.report.UnitDeviceReportExcelExporter;
import com.ruoyi.manage.report.UnitDeviceReportPdfExporter;
import com.ruoyi.manage.service.IUnitDeviceReportService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class UnitDeviceReportServiceImpl implements IUnitDeviceReportService
{
    private static final ZoneId REPORT_ZONE = ZoneId.of("Asia/Shanghai");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private UnitDeviceReportMapper unitDeviceReportMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Autowired
    private UnitDeviceReportExcelExporter excelExporter;

    @Autowired
    private UnitDeviceReportPdfExporter pdfExporter;

    @Override
    @DataScope(deptAlias = "d", permission = "report:unitDevice:list")
    public UnitDeviceReportPreview preview(UnitDeviceReportQuery query)
    {
        prepareQuery(query);
        return buildPreview(query);
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:unitDevice:export")
    public void exportExcel(HttpServletResponse response, UnitDeviceReportQuery query)
    {
        prepareQuery(query);
        excelExporter.export(response, buildPreview(query));
    }

    @Override
    @DataScope(deptAlias = "d", permission = "report:unitDevice:export")
    public void exportPdf(HttpServletResponse response, UnitDeviceReportQuery query)
    {
        prepareQuery(query);
        pdfExporter.export(response, buildPreview(query));
    }

    private void prepareQuery(UnitDeviceReportQuery query)
    {
        if (query == null)
        {
            throw new ServiceException("单位设备报告查询参数不能为空");
        }
        if (query.getDeptId() == null)
        {
            query.setDeptId(SecurityUtils.getDeptId());
        }
        if (query.getDeptId() == null)
        {
            throw new ServiceException("当前账号未设置所属单位，无法生成单位设备报告");
        }
        if (!SecurityUtils.isAdmin())
        {
            sysDeptService.checkDeptDataScope(query.getDeptId());
        }
        SysDept dept = sysDeptService.selectDeptById(query.getDeptId());
        if (dept == null || "2".equals(dept.getDelFlag()))
        {
            throw new ServiceException("所选单位不存在或已删除");
        }
        query.setScopeName(dept.getDeptName() + "及下级单位");
    }

    private UnitDeviceReportPreview buildPreview(UnitDeviceReportQuery query)
    {
        UnitDeviceReportPreview.UnitInfo unitInfo = unitDeviceReportMapper.selectUnitInfo(query);
        if (unitInfo == null)
        {
            throw new ServiceException("未查询到所选单位信息");
        }

        UnitDeviceReportPreview.Overview overview = unitDeviceReportMapper.selectOverview(query);
        if (overview == null)
        {
            overview = new UnitDeviceReportPreview.Overview();
        }
        // 缺失本地归属的数据不属于任何单位权限范围，不在普通单位报告中跨范围展示。
        overview.setMissingDeptCount(0L);

        UnitDeviceReportPreview preview = new UnitDeviceReportPreview();
        preview.setReportTitle(unitInfo.getDeptName() + "设备报告");
        preview.setScopeName(query.getScopeName());
        preview.setGeneratedTime(DATE_TIME.format(LocalDateTime.now(REPORT_ZONE)));
        preview.setUnitInfo(unitInfo);
        preview.setOverview(overview);
        preview.setFirePoints(nonNull(unitDeviceReportMapper.selectFirePoints(query)));
        preview.setGateways(nonNull(unitDeviceReportMapper.selectGateways(query)));
        preview.setSensors(nonNull(unitDeviceReportMapper.selectSensors(query)));
        preview.setExtinguishers(nonNull(unitDeviceReportMapper.selectExtinguishers(query)));
        return preview;
    }

    private <T> List<T> nonNull(List<T> values)
    {
        return values == null ? Collections.emptyList() : values;
    }
}
