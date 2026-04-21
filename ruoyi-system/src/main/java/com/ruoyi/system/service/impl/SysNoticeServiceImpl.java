package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.mapper.SysNoticeMapper;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysNoticeService;

/**
 * 公告 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysNoticeServiceImpl implements ISysNoticeService
{
    @Autowired
    private SysNoticeMapper noticeMapper;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectNoticeById(Long noticeId)
    {
        SysNotice notice = noticeMapper.selectNoticeById(noticeId);
        return attachPublishDeptIds(notice);
    }

    /**
     * 查询已发布公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    @Override
    public SysNotice selectPublishedNoticeById(Long noticeId)
    {
        SysNotice query = new SysNotice();
        query.setNoticeId(noticeId);
        query.setCurrentDeptId(SecurityUtils.getDeptId());
        SysNotice notice = noticeMapper.selectPublishedNoticeById(query);
        return attachPublishDeptIds(notice);
    }

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectNoticeList(SysNotice notice)
    {
        return noticeMapper.selectNoticeList(notice);
    }

    /**
     * 查询已发布公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    @Override
    public List<SysNotice> selectPublishedNoticeList(SysNotice notice)
    {
        notice.setCurrentDeptId(SecurityUtils.getDeptId());
        return noticeMapper.selectPublishedNoticeList(notice);
    }

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertNotice(SysNotice notice)
    {
        normalizeNoticePublishScope(notice);
        int rows = noticeMapper.insertNotice(notice);
        saveNoticeDeptScopes(notice);
        return rows;
    }

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateNotice(SysNotice notice)
    {
        normalizeNoticePublishScope(notice);
        noticeMapper.deleteNoticeDeptByNoticeId(notice.getNoticeId());
        saveNoticeDeptScopes(notice);
        return noticeMapper.updateNotice(notice);
    }

    /**
     * 删除公告对象
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeById(Long noticeId)
    {
        noticeMapper.deleteNoticeDeptByNoticeId(noticeId);
        return noticeMapper.deleteNoticeById(noticeId);
    }

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    @Override
    public int deleteNoticeByIds(Long[] noticeIds)
    {
        noticeMapper.deleteNoticeDeptByNoticeIds(noticeIds);
        return noticeMapper.deleteNoticeByIds(noticeIds);
    }

    private SysNotice attachPublishDeptIds(SysNotice notice)
    {
        if (notice != null)
        {
            notice.setPublishDeptIds(noticeMapper.selectNoticeDeptIdsByNoticeId(notice.getNoticeId()));
        }
        return notice;
    }

    private void normalizeNoticePublishScope(SysNotice notice)
    {
        String publishScopeType = StringUtils.defaultIfBlank(notice.getPublishScopeType(), "1");
        notice.setPublishScopeType(publishScopeType);
        if ("1".equals(publishScopeType))
        {
            notice.setPublishDeptIds(new ArrayList<Long>());
            return;
        }

        List<Long> publishDeptIds = notice.getPublishDeptIds();
        if (publishDeptIds == null || publishDeptIds.isEmpty())
        {
            throw new ServiceException("指定单位发布时，发布范围不能为空");
        }

        Set<Long> uniqueDeptIds = new LinkedHashSet<Long>();
        for (Long deptId : publishDeptIds)
        {
            if (deptId == null)
            {
                continue;
            }
            deptService.checkDeptDataScope(deptId);
            SysDept dept = deptService.selectDeptById(deptId);
            if (dept == null || !"0".equals(dept.getDelFlag()))
            {
                throw new ServiceException("存在无效的发布单位");
            }
            uniqueDeptIds.add(deptId);
        }

        if (uniqueDeptIds.isEmpty())
        {
            throw new ServiceException("指定单位发布时，发布范围不能为空");
        }

        notice.setPublishDeptIds(new ArrayList<Long>(uniqueDeptIds));
    }

    private void saveNoticeDeptScopes(SysNotice notice)
    {
        if ("2".equals(notice.getPublishScopeType()) && notice.getPublishDeptIds() != null && !notice.getPublishDeptIds().isEmpty())
        {
            noticeMapper.insertNoticeDeptScopes(notice);
        }
    }
}
