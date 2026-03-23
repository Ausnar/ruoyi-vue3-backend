package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysDeptApiConfigMapper;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 部门API配置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
@Service
public class SysDeptApiConfigServiceImpl implements ISysDeptApiConfigService
{
    @Autowired
    private SysDeptApiConfigMapper sysDeptApiConfigMapper;

    @Autowired
    private ISysDeptService deptService;

    /**
     * 查询部门API配置
     * 
     * @param configId 配置主键
     * @return 部门API配置
     */
    @Override
    public SysDeptApiConfig selectSysDeptApiConfigByConfigId(Long configId)
    {
        return sysDeptApiConfigMapper.selectSysDeptApiConfigByConfigId(configId);
    }

    /**
     * 查询部门API配置列表
     *
     * @param sysDeptApiConfig 部门API配置
     * @return 部门API配置
     */
    @Override
    public List<SysDeptApiConfig> selectSysDeptApiConfigList(SysDeptApiConfig sysDeptApiConfig)
    {
        // 管理员（userId=1）不限制，可以看到所有部门
        if (SecurityUtils.isAdmin())
        {
            return sysDeptApiConfigMapper.selectSysDeptApiConfigList(sysDeptApiConfig);
        }

        // 非管理员：只查询自己部门及下级部门的合同用户
        Long deptId = SecurityUtils.getDeptId();
        if (deptId != null)
        {
            // 获取当前部门及所有下级部门ID列表（实现"上级看下级"权限）
            List<Long> deptIds = deptService.selectDeptAndChildrenIds(deptId);
            sysDeptApiConfig.setDeptIds(deptIds);
        }
        return sysDeptApiConfigMapper.selectSysDeptApiConfigList(sysDeptApiConfig);
    }

    /**
     * 新增部门API配置
     * 
     * @param sysDeptApiConfig 部门API配置
     * @return 结果
     */
    @Override
    public int insertSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig)
    {
        // 检查部门是否已存在配置
        int count = sysDeptApiConfigMapper.checkDeptUnique(sysDeptApiConfig.getDeptId(), null);
        if (count > 0)
        {
            throw new ServiceException("该部门已存在API配置，一个部门只能配置一次");
        }
        
        sysDeptApiConfig.setCreateTime(DateUtils.getNowDate());
        return sysDeptApiConfigMapper.insertSysDeptApiConfig(sysDeptApiConfig);
    }

    /**
     * 修改部门API配置
     * 
     * @param sysDeptApiConfig 部门API配置
     * @return 结果
     */
    @Override
    public int updateSysDeptApiConfig(SysDeptApiConfig sysDeptApiConfig)
    {
        // 检查部门是否已存在配置（排除自己）
        int count = sysDeptApiConfigMapper.checkDeptUnique(
            sysDeptApiConfig.getDeptId(), 
            sysDeptApiConfig.getConfigId()
        );
        if (count > 0)
        {
            throw new ServiceException("该部门已存在API配置");
        }
        
        sysDeptApiConfig.setUpdateTime(DateUtils.getNowDate());
        return sysDeptApiConfigMapper.updateSysDeptApiConfig(sysDeptApiConfig);
    }

    /**
     * 批量删除部门API配置
     * 
     * @param configIds 需要删除的配置主键
     * @return 结果
     */
    @Override
    public int deleteSysDeptApiConfigByConfigIds(Long[] configIds)
    {
        return sysDeptApiConfigMapper.deleteSysDeptApiConfigByConfigIds(configIds);
    }

    /**
     * 删除部门API配置信息
     * 
     * @param configId 配置主键
     * @return 结果
     */
    @Override
    public int deleteSysDeptApiConfigByConfigId(Long configId)
    {
        return sysDeptApiConfigMapper.deleteSysDeptApiConfigByConfigId(configId);
    }
}