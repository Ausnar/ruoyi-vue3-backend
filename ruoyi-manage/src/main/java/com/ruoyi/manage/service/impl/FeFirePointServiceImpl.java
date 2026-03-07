package com.ruoyi.manage.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.manage.mapper.FeFirePointMapper;
import com.ruoyi.manage.domain.FeFirePoint;
import com.ruoyi.manage.service.IFeFirePointService;

/**
 * 消防点信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@Service
public class FeFirePointServiceImpl implements IFeFirePointService 
{
    @Autowired
    private FeFirePointMapper feFirePointMapper;

    /**
     * 查询消防点信息
     * 
     * @param firePointId 消防点信息主键
     * @return 消防点信息
     */
    @Override
    public FeFirePoint selectFeFirePointByFirePointId(Long firePointId)
    {
        return feFirePointMapper.selectFeFirePointByFirePointId(firePointId);
    }

    /**
     * 查询消防点信息列表
     * 
     * @param feFirePoint 消防点信息
     * @return 消防点信息
     */
    @Override
    public List<FeFirePoint> selectFeFirePointList(FeFirePoint feFirePoint)
    {
        // 非管理员只能查询自己部门的数据
        if (!SecurityUtils.isAdmin()) {
            feFirePoint.setDeptId(SecurityUtils.getDeptId());
        }
        return feFirePointMapper.selectFeFirePointList(feFirePoint);
    }

    /**
     * 新增消防点信息
     * 
     * @param feFirePoint 消防点信息
     * @return 结果
     */
    @Override
    public int insertFeFirePoint(FeFirePoint feFirePoint)
    {
        feFirePoint.setCreateTime(DateUtils.getNowDate());
        return feFirePointMapper.insertFeFirePoint(feFirePoint);
    }

    /**
     * 修改消防点信息
     * 
     * @param feFirePoint 消防点信息
     * @return 结果
     */
    @Override
    public int updateFeFirePoint(FeFirePoint feFirePoint)
    {
        feFirePoint.setUpdateTime(DateUtils.getNowDate());
        return feFirePointMapper.updateFeFirePoint(feFirePoint);
    }

    /**
     * 批量删除消防点信息
     * 
     * @param firePointIds 需要删除的消防点信息主键
     * @return 结果
     */
    @Override
    public int deleteFeFirePointByFirePointIds(Long[] firePointIds)
    {
        return feFirePointMapper.deleteFeFirePointByFirePointIds(firePointIds);
    }

    /**
     * 删除消防点信息信息
     * 
     * @param firePointId 消防点信息主键
     * @return 结果
     */
    @Override
    public int deleteFeFirePointByFirePointId(Long firePointId)
    {
        return feFirePointMapper.deleteFeFirePointByFirePointId(firePointId);
    }
}
