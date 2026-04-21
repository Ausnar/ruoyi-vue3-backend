package com.ruoyi.issue.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.issue.constant.IssueConstants;
import com.ruoyi.issue.domain.FeIssueItem;
import com.ruoyi.issue.domain.FeIssueItemLog;
import com.ruoyi.issue.domain.FeIssueProject;
import com.ruoyi.issue.domain.FeIssueProjectModule;
import com.ruoyi.issue.mapper.FeIssueItemLogMapper;
import com.ruoyi.issue.mapper.FeIssueItemMapper;
import com.ruoyi.issue.mapper.FeIssueProjectMapper;
import com.ruoyi.issue.mapper.FeIssueProjectModuleMapper;
import com.ruoyi.issue.service.IFeIssueItemService;

@Service
public class FeIssueItemServiceImpl implements IFeIssueItemService
{
    @Autowired
    private FeIssueItemMapper issueItemMapper;

    @Autowired
    private FeIssueItemLogMapper issueItemLogMapper;

    @Autowired
    private FeIssueProjectMapper issueProjectMapper;

    @Autowired
    private FeIssueProjectModuleMapper issueProjectModuleMapper;

    @Override
    public FeIssueItem selectFeIssueItemByIssueId(Long issueId)
    {
        FeIssueItem item = issueItemMapper.selectFeIssueItemByIssueId(issueId);
        if (item != null)
        {
            item.setLogs(issueItemLogMapper.selectFeIssueItemLogListByIssueId(issueId));
        }
        return item;
    }

    @Override
    public List<FeIssueItem> selectFeIssueItemList(FeIssueItem item)
    {
        return issueItemMapper.selectFeIssueItemList(item);
    }

    @Override
    public List<FeIssueItemLog> selectIssueLogs(Long issueId)
    {
        return issueItemLogMapper.selectFeIssueItemLogListByIssueId(issueId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertFeIssueItem(FeIssueItem item)
    {
        normalizeIssue(item);
        validateIssue(item, null, null);
        item.setDelFlag(IssueConstants.DEL_FLAG_NORMAL);
        item.setCreateBy(SecurityUtils.getUsername());
        item.setCreateTime(DateUtils.getNowDate());
        item.setUpdateBy(SecurityUtils.getUsername());
        item.setUpdateTime(DateUtils.getNowDate());
        int rows = issueItemMapper.insertFeIssueItem(item);
        insertLog(item.getIssueId(), null, item.getStatus(), null, item.getOwnerSide(),
            defaultValue(item.getChangeSummary(), "创建问题事项"));
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateFeIssueItem(FeIssueItem item)
    {
        FeIssueItem current = issueItemMapper.selectFeIssueItemByIssueId(item.getIssueId());
        if (current == null)
        {
            throw new ServiceException("未找到对应的问题事项");
        }
        normalizeIssue(item);
        validateIssue(item, current, current.getProjectId());
        if (StringUtils.isBlank(item.getStatus()))
        {
            item.setStatus(current.getStatus());
        }
        if (StringUtils.isBlank(item.getOwnerSide()))
        {
            item.setOwnerSide(current.getOwnerSide());
        }
        boolean changed = !StringUtils.equals(current.getStatus(), item.getStatus())
            || !StringUtils.equals(current.getOwnerSide(), item.getOwnerSide());
        if (changed && StringUtils.isBlank(item.getChangeSummary()))
        {
            throw new ServiceException("状态或责任侧变更时必须填写变更说明");
        }
        item.setUpdateBy(SecurityUtils.getUsername());
        item.setUpdateTime(DateUtils.getNowDate());
        int rows = issueItemMapper.updateFeIssueItem(item);
        if (changed)
        {
            insertLog(item.getIssueId(), current.getStatus(), item.getStatus(), current.getOwnerSide(),
                item.getOwnerSide(), item.getChangeSummary());
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFeIssueItemByIssueIds(Long[] issueIds)
    {
        int rows = 0;
        Date now = DateUtils.getNowDate();
        for (Long issueId : issueIds)
        {
            FeIssueItem current = issueItemMapper.selectFeIssueItemByIssueId(issueId);
            if (current == null)
            {
                continue;
            }
            current.setDelFlag(IssueConstants.DEL_FLAG_DELETED);
            current.setUpdateBy(SecurityUtils.getUsername());
            current.setUpdateTime(now);
            rows += issueItemMapper.deleteFeIssueItemByIssueId(current);
        }
        return rows;
    }

    private void normalizeIssue(FeIssueItem item)
    {
        if (item == null)
        {
            throw new ServiceException("问题事项参数不能为空");
        }
        item.setIssueTitle(StringUtils.trim(item.getIssueTitle()));
        item.setPriority(StringUtils.trim(item.getPriority()));
        item.setStatus(StringUtils.trim(item.getStatus()));
        item.setOwnerSide(StringUtils.trim(item.getOwnerSide()));
        item.setCurrentSummary(StringUtils.trim(item.getCurrentSummary()));
        item.setStatusDetail(StringUtils.trim(item.getStatusDetail()));
        item.setChangeSummary(StringUtils.trim(item.getChangeSummary()));
        item.setModuleNameSnapshot(StringUtils.trim(item.getModuleNameSnapshot()));
    }

    private void validateIssue(FeIssueItem item, FeIssueItem current, Long defaultProjectId)
    {
        if (item.getProjectId() == null)
        {
            item.setProjectId(defaultProjectId);
        }
        if (item.getProjectId() == null)
        {
            throw new ServiceException("所属项目不能为空");
        }
        FeIssueProject project = issueProjectMapper.selectFeIssueProjectByProjectId(item.getProjectId());
        if (project == null || !IssueConstants.DEL_FLAG_NORMAL.equals(project.getDelFlag()))
        {
            throw new ServiceException("所属项目不存在");
        }
        if (!IssueConstants.PROJECT_STATUS_ACTIVE.equals(project.getStatus())
            && (current == null || !project.getProjectId().equals(current.getProjectId())))
        {
            throw new ServiceException("当前项目已停用，无法新建问题事项");
        }
        if (StringUtils.isBlank(item.getIssueTitle()))
        {
            throw new ServiceException("问题不能为空");
        }
        if (item.getModuleId() == null)
        {
            throw new ServiceException("所属模块不能为空");
        }
        FeIssueProjectModule module = issueProjectModuleMapper.selectFeIssueProjectModuleByModuleId(item.getModuleId());
        if (module == null || !IssueConstants.DEL_FLAG_NORMAL.equals(module.getDelFlag()))
        {
            throw new ServiceException("所属模块不存在");
        }
        if (!module.getProjectId().equals(item.getProjectId()))
        {
            throw new ServiceException("模块不属于当前项目");
        }
        if (!IssueConstants.MODULE_STATUS_ACTIVE.equals(module.getStatus())
            && (current == null || !module.getModuleId().equals(current.getModuleId())))
        {
            throw new ServiceException("当前模块已停用，无法新建问题事项");
        }
        item.setModuleNameSnapshot(module.getModuleName());
        if (StringUtils.isBlank(item.getModuleNameSnapshot()))
        {
            throw new ServiceException("模块快照不能为空");
        }
        if (StringUtils.isBlank(item.getPriority()) || !IssueConstants.PRIORITY_SET.contains(item.getPriority()))
        {
            throw new ServiceException("优先级不合法");
        }
        if (StringUtils.isBlank(item.getStatus()) || !IssueConstants.ISSUE_STATUS_SET.contains(item.getStatus()))
        {
            throw new ServiceException("当前状态不合法");
        }
        if (StringUtils.isBlank(item.getOwnerSide()) || !IssueConstants.OWNER_SIDE_SET.contains(item.getOwnerSide()))
        {
            throw new ServiceException("责任侧不合法");
        }
        if (StringUtils.isBlank(item.getCurrentSummary()))
        {
            throw new ServiceException("说明不能为空");
        }
    }

    private void insertLog(Long issueId, String fromStatus, String toStatus, String fromOwnerSide, String toOwnerSide,
        String changeSummary)
    {
        FeIssueItemLog log = new FeIssueItemLog();
        log.setIssueId(issueId);
        log.setFromStatus(fromStatus);
        log.setToStatus(toStatus);
        log.setFromOwnerSide(fromOwnerSide);
        log.setToOwnerSide(toOwnerSide);
        log.setChangeSummary(changeSummary);
        log.setOperator(SecurityUtils.getUsername());
        log.setCreateTime(DateUtils.getNowDate());
        issueItemLogMapper.insertFeIssueItemLog(log);
    }

    private String defaultValue(String value, String defaultValue)
    {
        return StringUtils.isBlank(value) ? defaultValue : value;
    }
}
