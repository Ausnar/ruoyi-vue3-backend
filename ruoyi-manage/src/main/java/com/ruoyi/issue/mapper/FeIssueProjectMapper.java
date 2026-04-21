package com.ruoyi.issue.mapper;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueProject;

public interface FeIssueProjectMapper
{
    FeIssueProject selectFeIssueProjectByProjectId(Long projectId);

    FeIssueProject selectByProjectCode(String projectCode);

    FeIssueProject selectByProjectName(String projectName);

    List<FeIssueProject> selectFeIssueProjectList(FeIssueProject project);

    int insertFeIssueProject(FeIssueProject project);

    int updateFeIssueProject(FeIssueProject project);

    int deleteFeIssueProjectByProjectId(Long projectId);
}
