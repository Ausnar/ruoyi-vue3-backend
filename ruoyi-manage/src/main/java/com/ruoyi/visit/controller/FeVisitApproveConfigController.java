package com.ruoyi.visit.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.visit.domain.FeVisitApproveConfig;
import com.ruoyi.visit.service.IFeVisitApproveConfigService;

@RestController
@RequestMapping("/visit/approve-config")
public class FeVisitApproveConfigController extends BaseController
{
    @Autowired
    private IFeVisitApproveConfigService feVisitApproveConfigService;

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeVisitApproveConfig config)
    {
        startPage();
        List<FeVisitApproveConfig> list = feVisitApproveConfigService.selectFeVisitApproveConfigList(config);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:query')")
    @GetMapping("/{configId}")
    public AjaxResult getInfo(@PathVariable Long configId)
    {
        return success(feVisitApproveConfigService.selectFeVisitApproveConfigByConfigId(configId));
    }

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:list')")
    @GetMapping("/role-options")
    public AjaxResult roleOptions()
    {
        List<SysRole> roles = feVisitApproveConfigService.selectRoleOptions();
        return success(roles);
    }

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:add')")
    @Log(title = "外出拜访审批配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeVisitApproveConfig config)
    {
        return toAjax(feVisitApproveConfigService.insertFeVisitApproveConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:edit')")
    @Log(title = "外出拜访审批配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeVisitApproveConfig config)
    {
        return toAjax(feVisitApproveConfigService.updateFeVisitApproveConfig(config));
    }

    @PreAuthorize("@ss.hasPermi('visit:approveConfig:remove')")
    @Log(title = "外出拜访审批配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable Long[] configIds)
    {
        return toAjax(feVisitApproveConfigService.deleteFeVisitApproveConfigByConfigIds(configIds));
    }
}
