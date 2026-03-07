package com.ruoyi.web.controller.system;


import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysDeptApiConfig;
import com.ruoyi.system.service.ISysDeptApiConfigService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合同管理Controller
 * 
 * @author ruoyi
 * @date 2026-02-25
 */
@RestController
@RequestMapping("/system/contract")
public class SysDeptApiConfigController extends BaseController
{
    @Autowired
    private ISysDeptApiConfigService sysDeptApiConfigService;

    /**
     * 查询合同管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(SysDeptApiConfig sysDeptApiConfig)
    {
        startPage();
        List<SysDeptApiConfig> list = sysDeptApiConfigService.selectSysDeptApiConfigList(sysDeptApiConfig);
        return getDataTable(list);
    }

    /**
     * 导出合同管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:contract:export')")
    @Log(title = "合同管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDeptApiConfig sysDeptApiConfig)
    {
        List<SysDeptApiConfig> list = sysDeptApiConfigService.selectSysDeptApiConfigList(sysDeptApiConfig);
        ExcelUtil<SysDeptApiConfig> util = new ExcelUtil<SysDeptApiConfig>(SysDeptApiConfig.class);
        util.exportExcel(response, list, "合同管理数据");
    }

    /**
     * 获取合同管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:contract:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return success(sysDeptApiConfigService.selectSysDeptApiConfigByConfigId(configId));
    }

    /**
     * 新增合同管理
     */
    @PreAuthorize("@ss.hasPermi('system:contract:add')")
    @Log(title = "合同管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysDeptApiConfig sysDeptApiConfig)
    {
        return toAjax(sysDeptApiConfigService.insertSysDeptApiConfig(sysDeptApiConfig));
    }

    /**
     * 修改合同管理
     */
    @PreAuthorize("@ss.hasPermi('system:contract:edit')")
    @Log(title = "合同管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysDeptApiConfig sysDeptApiConfig)
    {
        return toAjax(sysDeptApiConfigService.updateSysDeptApiConfig(sysDeptApiConfig));
    }

    /**
     * 删除合同管理
     */
    @PreAuthorize("@ss.hasPermi('system:contract:remove')")
    @Log(title = "合同管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(sysDeptApiConfigService.deleteSysDeptApiConfigByConfigIds(configIds));
    }
}
