package com.ruoyi.issue.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.issue.constant.IssueConstants;
import com.ruoyi.issue.domain.FeIssueProject;
import com.ruoyi.issue.mapper.FeIssueProjectMapper;
import com.ruoyi.issue.service.IFeIssueProjectService;

@Service
public class FeIssueProjectServiceImpl implements IFeIssueProjectService
{
    @Autowired
    private FeIssueProjectMapper issueProjectMapper;

    @Override
    public FeIssueProject selectFeIssueProjectByProjectId(Long projectId)
    {
        return issueProjectMapper.selectFeIssueProjectByProjectId(projectId);
    }

    @Override
    public List<FeIssueProject> selectFeIssueProjectList(FeIssueProject project)
    {
        return issueProjectMapper.selectFeIssueProjectList(project);
    }

    @Override
    public List<FeIssueProject> selectActiveProjectOptions()
    {
        FeIssueProject query = new FeIssueProject();
        query.setStatus(IssueConstants.PROJECT_STATUS_ACTIVE);
        return issueProjectMapper.selectFeIssueProjectList(query);
    }

    @Override
    public int insertFeIssueProject(FeIssueProject project)
    {
        normalizeProject(project);
        validateProject(project, null);
        project.setStatus(defaultValue(project.getStatus(), IssueConstants.PROJECT_STATUS_ACTIVE));
        project.setDelFlag(IssueConstants.DEL_FLAG_NORMAL);
        project.setCreateBy(SecurityUtils.getUsername());
        project.setCreateTime(DateUtils.getNowDate());
        project.setUpdateBy(SecurityUtils.getUsername());
        project.setUpdateTime(DateUtils.getNowDate());
        return issueProjectMapper.insertFeIssueProject(project);
    }

    @Override
    public int updateFeIssueProject(FeIssueProject project)
    {
        FeIssueProject current = issueProjectMapper.selectFeIssueProjectByProjectId(project.getProjectId());
        if (current == null)
        {
            throw new ServiceException("未找到对应的项目");
        }
        normalizeProject(project);
        validateProject(project, current.getProjectId());
        if (StringUtils.isBlank(project.getStatus()))
        {
            project.setStatus(current.getStatus());
        }
        project.setUpdateBy(SecurityUtils.getUsername());
        project.setUpdateTime(DateUtils.getNowDate());
        return issueProjectMapper.updateFeIssueProject(project);
    }

    @Override
    public int toggleFeIssueProject(Long projectId)
    {
        FeIssueProject current = issueProjectMapper.selectFeIssueProjectByProjectId(projectId);
        if (current == null)
        {
            throw new ServiceException("未找到对应的项目");
        }
        current.setStatus(IssueConstants.PROJECT_STATUS_ACTIVE.equals(current.getStatus())
            ? IssueConstants.PROJECT_STATUS_INACTIVE
            : IssueConstants.PROJECT_STATUS_ACTIVE);
        current.setUpdateBy(SecurityUtils.getUsername());
        current.setUpdateTime(DateUtils.getNowDate());
        return issueProjectMapper.updateFeIssueProject(current);
    }

    private void normalizeProject(FeIssueProject project)
    {
        if (project == null)
        {
            throw new ServiceException("项目参数不能为空");
        }
        project.setProjectName(StringUtils.trim(project.getProjectName()));
        project.setProjectCode(StringUtils.trim(project.getProjectCode()));
    }

    private void validateProject(FeIssueProject project, Long excludeProjectId)
    {
        if (StringUtils.isBlank(project.getProjectName()))
        {
            throw new ServiceException("项目名称不能为空");
        }
        if (StringUtils.isBlank(project.getProjectCode()))
        {
            throw new ServiceException("项目编码不能为空");
        }
        if (!IssueConstants.PROJECT_STATUS_SET.contains(defaultValue(project.getStatus(), IssueConstants.PROJECT_STATUS_ACTIVE)))
        {
            throw new ServiceException("项目状态不合法");
        }
        FeIssueProject projectByCode = issueProjectMapper.selectByProjectCode(project.getProjectCode());
        if (projectByCode != null && !projectByCode.getProjectId().equals(excludeProjectId))
        {
            throw new ServiceException("项目编码已存在");
        }
        FeIssueProject projectByName = issueProjectMapper.selectByProjectName(project.getProjectName());
        if (projectByName != null && !projectByName.getProjectId().equals(excludeProjectId))
        {
            throw new ServiceException("项目名称已存在");
        }
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
