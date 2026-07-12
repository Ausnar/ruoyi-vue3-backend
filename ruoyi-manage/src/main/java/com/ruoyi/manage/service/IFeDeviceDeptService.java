package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;

public interface IFeDeviceDeptService
{
    public List<TreeSelect> selectDeviceDeptTreeList(SysDept dept);
}
