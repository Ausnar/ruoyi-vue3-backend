package com.ruoyi.issue.mapper;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueItemLog;

public interface FeIssueItemLogMapper
{
    List<FeIssueItemLog> selectFeIssueItemLogListByIssueId(Long issueId);

    int insertFeIssueItemLog(FeIssueItemLog log);
}
