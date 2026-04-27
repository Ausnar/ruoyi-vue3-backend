package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.dashboard.DashboardMapFirePoint;

public interface DashboardMapMapper
{
    List<DashboardMapFirePoint> selectFirePointsForMap(@Param("deptIds") List<Long> deptIds);
}
