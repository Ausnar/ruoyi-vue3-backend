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
import com.ruoyi.issue.domain.FeIssueProjectModule;
import com.ruoyi.issue.mapper.FeIssueProjectMapper;
import com.ruoyi.issue.mapper.FeIssueProjectModuleMapper;
import com.ruoyi.issue.service.IFeIssueProjectModuleService;

@Service
public class FeIssueProjectModuleServiceImpl implements IFeIssueProjectModuleService
{
    @Autowired
    private FeIssueProjectModuleMapper issueProjectModuleMapper;

    @Autowired
    private FeIssueProjectMapper issueProjectMapper;

    @Override
    public FeIssueProjectModule selectFeIssueProjectModuleByModuleId(Long moduleId)
    {
        return issueProjectModuleMapper.selectFeIssueProjectModuleByModuleId(moduleId);
    }

    @Override
    public List<FeIssueProjectModule> selectFeIssueProjectModuleList(FeIssueProjectModule module)
    {
        return issueProjectModuleMapper.selectFeIssueProjectModuleList(module);
    }

    @Override
    public List<FeIssueProjectModule> selectActiveModuleOptions(Long projectId)
    {
        if (projectId == null)
        {
            throw new ServiceException("项目不能为空");
        }
        FeIssueProjectModule query = new FeIssueProjectModule();
        query.setProjectId(projectId);
        query.setStatus(IssueConstants.MODULE_STATUS_ACTIVE);
        return issueProjectModuleMapper.selectFeIssueProjectModuleList(query);
    }

    @Override
    public int insertFeIssueProjectModule(FeIssueProjectModule module)
    {
        normalizeModule(module);
        validateModule(module, null);
        module.setStatus(defaultValue(module.getStatus(), IssueConstants.MODULE_STATUS_ACTIVE));
        module.setSortOrder(defaultSortOrder(module.getSortOrder()));
        module.setDelFlag(IssueConstants.DEL_FLAG_NORMAL);
        module.setCreateBy(SecurityUtils.getUsername());
        module.setCreateTime(DateUtils.getNowDate());
        module.setUpdateBy(SecurityUtils.getUsername());
        module.setUpdateTime(DateUtils.getNowDate());
        return issueProjectModuleMapper.insertFeIssueProjectModule(module);
    }

    @Override
    public int updateFeIssueProjectModule(FeIssueProjectModule module)
    {
        FeIssueProjectModule current = issueProjectModuleMapper.selectFeIssueProjectModuleByModuleId(module.getModuleId());
        if (current == null)
        {
            throw new ServiceException("未找到对应的项目模块");
        }
        normalizeModule(module);
        validateModule(module, current.getModuleId());
        if (module.getProjectId() == null)
        {
            module.setProjectId(current.getProjectId());
        }
        if (StringUtils.isBlank(module.getStatus()))
        {
            module.setStatus(current.getStatus());
        }
        if (module.getSortOrder() == null)
        {
            module.setSortOrder(current.getSortOrder());
        }
        module.setUpdateBy(SecurityUtils.getUsername());
        module.setUpdateTime(DateUtils.getNowDate());
        return issueProjectModuleMapper.updateFeIssueProjectModule(module);
    }

    @Override
    public int toggleFeIssueProjectModule(Long moduleId)
    {
        FeIssueProjectModule current = issueProjectModuleMapper.selectFeIssueProjectModuleByModuleId(moduleId);
        if (current == null)
        {
            throw new ServiceException("未找到对应的项目模块");
        }
        current.setStatus(IssueConstants.MODULE_STATUS_ACTIVE.equals(current.getStatus())
            ? IssueConstants.MODULE_STATUS_INACTIVE
            : IssueConstants.MODULE_STATUS_ACTIVE);
        current.setUpdateBy(SecurityUtils.getUsername());
        current.setUpdateTime(DateUtils.getNowDate());
        return issueProjectModuleMapper.updateFeIssueProjectModule(current);
    }

    private void normalizeModule(FeIssueProjectModule module)
    {
        if (module == null)
        {
            throw new ServiceException("项目模块参数不能为空");
        }
        module.setModuleName(StringUtils.trim(module.getModuleName()));
    }

    private void validateModule(FeIssueProjectModule module, Long excludeModuleId)
    {
        if (module.getProjectId() == null)
        {
            throw new ServiceException("所属项目不能为空");
        }
        FeIssueProject project = issueProjectMapper.selectFeIssueProjectByProjectId(module.getProjectId());
        if (project == null || !IssueConstants.DEL_FLAG_NORMAL.equals(project.getDelFlag()))
        {
            throw new ServiceException("所属项目不存在");
        }
        if (!IssueConstants.PROJECT_STATUS_ACTIVE.equals(project.getStatus()))
        {
            throw new ServiceException("所属项目已停用，无法维护项目模块");
        }
        if (StringUtils.isBlank(module.getModuleName()))
        {
            throw new ServiceException("模块名称不能为空");
        }
        String status = defaultValue(module.getStatus(), IssueConstants.MODULE_STATUS_ACTIVE);
        if (!IssueConstants.MODULE_STATUS_SET.contains(status))
        {
            throw new ServiceException("模块状态不合法");
        }
        FeIssueProjectModule moduleByName =
            issueProjectModuleMapper.selectByProjectIdAndModuleName(module.getProjectId(), module.getModuleName());
        if (moduleByName != null && !moduleByName.getModuleId().equals(excludeModuleId))
        {
            throw new ServiceException("同一项目下模块名称已存在");
        }
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }

    private Integer defaultSortOrder(Integer sortOrder)
    {
        return sortOrder == null ? 1 : sortOrder;
    }
}
