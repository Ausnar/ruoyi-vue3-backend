package com.ruoyi.manage.mapper;

import java.util.List;
import com.ruoyi.manage.domain.FeFirePoint;

/**
 * 消防点信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
public interface FeFirePointMapper 
{
    /**
     * 查询消防点信息
     * 
     * @param firePointId 消防点信息主键
     * @return 消防点信息
     */
    public FeFirePoint selectFeFirePointByFirePointId(Long firePointId);

    /**
     * 查询消防点信息列表
     * 
     * @param feFirePoint 消防点信息
     * @return 消防点信息集合
     */
    public List<FeFirePoint> selectFeFirePointList(FeFirePoint feFirePoint);

    /**
     * 新增消防点信息
     * 
     * @param feFirePoint 消防点信息
     * @return 结果
     */
    public int insertFeFirePoint(FeFirePoint feFirePoint);

    /**
     * 修改消防点信息
     * 
     * @param feFirePoint 消防点信息
     * @return 结果
     */
    public int updateFeFirePoint(FeFirePoint feFirePoint);

    /**
     * 删除消防点信息
     * 
     * @param firePointId 消防点信息主键
     * @return 结果
     */
    public int deleteFeFirePointByFirePointId(Long firePointId);

    /**
     * 批量删除消防点信息
     * 
     * @param firePointIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteFeFirePointByFirePointIds(Long[] firePointIds);
}
