package com.ruoyi.system.domain;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.xss.Xss;

/**
 * 通知公告表 sys_notice
 * 
 * @author ruoyi
 */
public class SysNotice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 公告ID */
    private Long noticeId;

    /** 公告标题 */
    private String noticeTitle;

    /** 公告类型（1通知 2公告） */
    private String noticeType;

    /** 公告内容 */
    private String noticeContent;

    /** 公告状态（0正常 1关闭） */
    private String status;

    /** 发布范围类型（1全体 2指定部门） */
    private String publishScopeType;

    /** 发布部门ID集合 */
    private List<Long> publishDeptIds;

    /** 发布部门名称 */
    private String publishDeptNames;

    /** 当前登录用户部门ID */
    private Long currentDeptId;

    public Long getNoticeId()
    {
        return noticeId;
    }

    public void setNoticeId(Long noticeId)
    {
        this.noticeId = noticeId;
    }

    public void setNoticeTitle(String noticeTitle)
    {
        this.noticeTitle = noticeTitle;
    }

    @Xss(message = "公告标题不能包含脚本字符")
    @NotBlank(message = "公告标题不能为空")
    @Size(min = 0, max = 50, message = "公告标题不能超过50个字符")
    public String getNoticeTitle()
    {
        return noticeTitle;
    }

    public void setNoticeType(String noticeType)
    {
        this.noticeType = noticeType;
    }

    public String getNoticeType()
    {
        return noticeType;
    }

    public void setNoticeContent(String noticeContent)
    {
        this.noticeContent = noticeContent;
    }

    public String getNoticeContent()
    {
        return noticeContent;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getStatus()
    {
        return status;
    }

    public void setPublishScopeType(String publishScopeType)
    {
        this.publishScopeType = publishScopeType;
    }

    public String getPublishScopeType()
    {
        return publishScopeType;
    }

    public void setPublishDeptIds(List<Long> publishDeptIds)
    {
        this.publishDeptIds = publishDeptIds;
    }

    public List<Long> getPublishDeptIds()
    {
        return publishDeptIds;
    }

    public void setPublishDeptNames(String publishDeptNames)
    {
        this.publishDeptNames = publishDeptNames;
    }

    public String getPublishDeptNames()
    {
        return publishDeptNames;
    }

    public void setCurrentDeptId(Long currentDeptId)
    {
        this.currentDeptId = currentDeptId;
    }

    public Long getCurrentDeptId()
    {
        return currentDeptId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("noticeId", getNoticeId())
            .append("noticeTitle", getNoticeTitle())
            .append("noticeType", getNoticeType())
            .append("noticeContent", getNoticeContent())
            .append("status", getStatus())
            .append("publishScopeType", getPublishScopeType())
            .append("publishDeptIds", getPublishDeptIds())
            .append("publishDeptNames", getPublishDeptNames())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
