package com.ruoyi.manage.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.manage.domain.FeFirePoint;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.manage.service.IFeFirePointService;
import com.ruoyi.system.service.ISysDeptService;

@Primary
@Service
public class FeFirePointScopedServiceImpl implements IFeFirePointService
{
    @Autowired
    private FeFirePointMapper feFirePointMapper;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public FeFirePoint selectFeFirePointByFirePointId(Long firePointId)
    {
        FeFirePoint firePoint = feFirePointMapper.selectFeFirePointByFirePointId(firePointId);
        checkDeptDataScope(firePoint == null ? null : firePoint.getDeptId());
        return firePoint;
    }

    @Override
    @DataScope(deptAlias = "f")
    public List<FeFirePoint> selectFeFirePointList(FeFirePoint feFirePoint)
    {
        return feFirePointMapper.selectFeFirePointList(feFirePoint);
    }

    @Override
    public int insertFeFirePoint(FeFirePoint feFirePoint)
    {
        validateTargetDept(feFirePoint.getDeptId());
        feFirePoint.setCreateTime(DateUtils.getNowDate());
        return feFirePointMapper.insertFeFirePoint(feFirePoint);
    }

    @Override
    public int updateFeFirePoint(FeFirePoint feFirePoint)
    {
        selectFeFirePointByFirePointId(feFirePoint.getFirePointId());
        validateTargetDept(feFirePoint.getDeptId());
        feFirePoint.setUpdateTime(DateUtils.getNowDate());
        return feFirePointMapper.updateFeFirePoint(feFirePoint);
    }

    @Override
    public int deleteFeFirePointByFirePointIds(Long[] firePointIds)
    {
        for (Long firePointId : firePointIds)
        {
            selectFeFirePointByFirePointId(firePointId);
        }
        return feFirePointMapper.deleteFeFirePointByFirePointIds(firePointIds);
    }

    @Override
    public int deleteFeFirePointByFirePointId(Long firePointId)
    {
        selectFeFirePointByFirePointId(firePointId);
        return feFirePointMapper.deleteFeFirePointByFirePointId(firePointId);
    }

    private void validateTargetDept(Long deptId)
    {
        if (!SecurityUtils.isAdmin() && deptId != null)
        {
            sysDeptService.checkDeptDataScope(deptId);
        }
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
