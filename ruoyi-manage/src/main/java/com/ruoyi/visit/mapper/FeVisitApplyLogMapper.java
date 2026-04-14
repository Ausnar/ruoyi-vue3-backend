package com.ruoyi.visit.mapper;

import java.util.List;
import com.ruoyi.visit.domain.FeVisitApplyLog;

public interface FeVisitApplyLogMapper
{
    List<FeVisitApplyLog> selectFeVisitApplyLogListByVisitId(Long visitId);

    int insertFeVisitApplyLog(FeVisitApplyLog log);
}
