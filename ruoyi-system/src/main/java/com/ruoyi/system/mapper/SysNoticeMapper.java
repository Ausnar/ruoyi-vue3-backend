package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysNotice;

/**
 * 通知公告表 数据层
 * 
 * @author ruoyi
 */
public interface SysNoticeMapper
{
    /**
     * 查询公告信息
     * 
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectNoticeById(Long noticeId);

    /**
     * 查询已发布公告信息
     *
     * @param noticeId 公告ID
     * @return 公告信息
     */
    public SysNotice selectPublishedNoticeById(SysNotice notice);

    /**
     * 查询公告列表
     * 
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectNoticeList(SysNotice notice);

    /**
     * 查询已发布公告列表
     *
     * @param notice 公告信息
     * @return 公告集合
     */
    public List<SysNotice> selectPublishedNoticeList(SysNotice notice);

    /**
     * 新增公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNotice(SysNotice notice);

    /**
     * 新增公告发布部门范围
     *
     * @param notice 公告信息
     * @return 结果
     */
    public int insertNoticeDeptScopes(SysNotice notice);

    /**
     * 修改公告
     * 
     * @param notice 公告信息
     * @return 结果
     */
    public int updateNotice(SysNotice notice);

    /**
     * 查询公告发布部门ID集合
     *
     * @param noticeId 公告ID
     * @return 部门ID集合
     */
    public List<Long> selectNoticeDeptIdsByNoticeId(Long noticeId);

    /**
     * 删除公告发布部门范围
     *
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeDeptByNoticeId(Long noticeId);

    /**
     * 批量删除公告发布部门范围
     *
     * @param noticeIds 公告ID数组
     * @return 结果
     */
    public int deleteNoticeDeptByNoticeIds(Long[] noticeIds);

    /**
     * 批量删除公告
     * 
     * @param noticeId 公告ID
     * @return 结果
     */
    public int deleteNoticeById(Long noticeId);

    /**
     * 批量删除公告信息
     * 
     * @param noticeIds 需要删除的公告ID
     * @return 结果
     */
    public int deleteNoticeByIds(Long[] noticeIds);
}
