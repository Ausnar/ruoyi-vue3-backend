package com.ruoyi.issue.service;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueProjectModule;

public interface IFeIssueProjectModuleService
{
    FeIssueProjectModule selectFeIssueProjectModuleByModuleId(Long moduleId);

    List<FeIssueProjectModule> selectFeIssueProjectModuleList(FeIssueProjectModule module);

    List<FeIssueProjectModule> selectActiveModuleOptions(Long projectId);

    int insertFeIssueProjectModule(FeIssueProjectModule module);

    int updateFeIssueProjectModule(FeIssueProjectModule module);

    int toggleFeIssueProjectModule(Long moduleId);
}
