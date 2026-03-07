package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysDeptApiConfig;

/**
 * 合同管理Service接口
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
public interface ISysDeptApiConfigService 
{
    /**
     * 查询合同管理
     * 
     * @param configId 合同管理主键
     * @return 合同管理
     */
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId);

    /**
     * 查询合同管理列表
     * 
     * @param sysDeptApiConfig 合同管理
     * @return 合同管理集合
     */
    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 新增合同管理
     * 
     * @param sysDeptApiConfig 合同管理
     * @return 结果
     */
    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 修改合同管理
     * 
     * @param sysDeptApiConfig 合同管理
     * @return 结果
     */
    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 批量删除合同管理
     * 
     * @param configIds 需要删除的合同管理主键集合
     * @return 结果
     */
    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds);

    /**
     * 删除合同管理信息
     * 
     * @param configId 合同管理主键
     * @return 结果
     */
    public int deleteSysDeptApiConfigByConfigId(Long configId);
}
