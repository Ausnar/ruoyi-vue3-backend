package com.ruoyi.issue.mapper;

import java.util.List;
import com.ruoyi.issue.domain.FeIssueItem;

public interface FeIssueItemMapper
{
    FeIssueItem selectFeIssueItemByIssueId(Long issueId);

    List<FeIssueItem> selectFeIssueItemList(FeIssueItem item);

    int insertFeIssueItem(FeIssueItem item);

    int updateFeIssueItem(FeIssueItem item);

    int deleteFeIssueItemByIssueId(FeIssueItem item);
}
