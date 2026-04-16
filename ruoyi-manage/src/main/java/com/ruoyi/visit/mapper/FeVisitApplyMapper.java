package com.ruoyi.visit.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.visit.domain.FeVisitApply;
import com.ruoyi.visit.domain.FeVisitContractOption;

public interface FeVisitApplyMapper
{
    FeVisitApply selectFeVisitApplyByVisitId(Long visitId);

    List<FeVisitApply> selectMyVisitApplyList(FeVisitApply apply);

    List<FeVisitApply> selectApproveVisitApplyList(FeVisitApply apply);

    List<FeVisitContractOption> selectContractOptions(@Param("scopeDeptIds") List<Long> scopeDeptIds);

    List<FeVisitContractOption> selectContractOptionsByDeptIds(@Param("deptIds") List<Long> deptIds);

    FeVisitContractOption selectContractOptionByConfigId(Long configId);

    String selectLatestVisitNo(@Param("prefix") String prefix);

    int insertFeVisitApply(FeVisitApply apply);

    int updateFeVisitApply(FeVisitApply apply);
}
