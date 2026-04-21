package com.ruoyi.issue.service;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueProject;

public interface IFeIssueProjectService
{
    FeIssueProject selectFeIssueProjectByProjectId(Long projectId);

    List<FeIssueProject> selectFeIssueProjectList(FeIssueProject project);

    List<FeIssueProject> selectActiveProjectOptions();

    int insertFeIssueProject(FeIssueProject project);

    int updateFeIssueProject(FeIssueProject project);

    int toggleFeIssueProject(Long projectId);
}
