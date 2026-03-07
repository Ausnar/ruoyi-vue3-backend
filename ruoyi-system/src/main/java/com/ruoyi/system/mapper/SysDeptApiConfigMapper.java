package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDeptApiConfig;
import org.apache.ibatis.annotations.Param;

/**
 * 部门API配置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
public interface SysDeptApiConfigMapper 
{
    /**
     * 查询部门API配置
     * 
     * @param configId 配置主键
     * @return 部门API配置
     */
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId);

    /**
     * 查询部门API配置列表
     * 
     * @param sysDeptApiConfig 部门API配置
     * @return 部门API配置集合
     */
    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 新增部门API配置
     * 
     * @param sysDeptApiConfig 部门API配置
     * @return 结果
     */
    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 修改部门API配置
     * 
     * @param sysDeptApiConfig 部门API配置
     * @return 结果
     */
    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig);

    /**
     * 删除部门API配置
     * 
     * @param configId 配置主键
     * @return 结果
     */
    public int deleteSysDeptApiConfigByConfigId(Long configId);

    /**
     * 批量删除部门API配置
     * 
     * @param configIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds);

    /**
     * 检查部门是否已存在配置
     * 
     * @param deptId 部门ID
     * @param configId 配置ID（修改时传入，用于排除自己）
     * @return 结果 >0 表示已存在
     */
    public int checkDeptUnique(@Param("deptId") Long deptId, @Param("configId") Long configId);
}