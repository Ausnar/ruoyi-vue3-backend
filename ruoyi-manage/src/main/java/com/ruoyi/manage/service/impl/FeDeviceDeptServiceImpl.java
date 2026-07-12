package com.ruoyi.manage.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.manage.mapper.FeDeviceDeptMapper;
import com.ruoyi.manage.service.IFeDeviceDeptService;
import com.ruoyi.system.service.ISysDeptService;

@Service
public class FeDeviceDeptServiceImpl implements IFeDeviceDeptService
{
    @Autowired
    private FeDeviceDeptMapper deviceDeptMapper;

    @Autowired
    private ISysDeptService deptService;

    @Override
    public List<TreeSelect> selectDeviceDeptTreeList(SysDept dept)
    {
        List<SysDept> deviceDepts = deviceDeptMapper.selectDeviceDeptList(dept);
        List<SysDept> scopedDepts = deptService.selectDeptList(new SysDept());
        Set<Long> scopedDeptIds = scopedDepts.stream().map(SysDept::getDeptId).collect(Collectors.toSet());
        deviceDepts = deviceDepts.stream()
                .filter(item -> scopedDeptIds.contains(item.getDeptId()))
                .collect(Collectors.toList());
        return deptService.buildDeptTreeSelect(deviceDepts);
    }
}
