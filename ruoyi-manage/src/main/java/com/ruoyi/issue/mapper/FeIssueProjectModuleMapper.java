package com.ruoyi.issue.mapper;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueProjectModule;

public interface FeIssueProjectModuleMapper
{
    FeIssueProjectModule selectFeIssueProjectModuleByModuleId(Long moduleId);

    List<FeIssueProjectModule> selectFeIssueProjectModuleList(FeIssueProjectModule module);

    FeIssueProjectModule selectByProjectIdAndModuleName(Long projectId, String moduleName);

    int insertFeIssueProjectModule(FeIssueProjectModule module);

    int updateFeIssueProjectModule(FeIssueProjectModule module);
}
