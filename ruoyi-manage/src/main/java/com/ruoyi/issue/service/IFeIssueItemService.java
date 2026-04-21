package com.ruoyi.issue.service;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueItem;
import com.ruoyi.issue.domain.FeIssueItemLog;

public interface IFeIssueItemService
{
    FeIssueItem selectFeIssueItemByIssueId(Long issueId);

    List<FeIssueItem> selectFeIssueItemList(FeIssueItem item);

    List<FeIssueItemLog> selectIssueLogs(Long issueId);

    int insertFeIssueItem(FeIssueItem item);

    int updateFeIssueItem(FeIssueItem item);

    int deleteFeIssueItemByIssueIds(Long[] issueIds);
}
