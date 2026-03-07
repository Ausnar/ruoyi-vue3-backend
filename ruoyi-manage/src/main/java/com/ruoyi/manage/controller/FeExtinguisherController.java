package com.ruoyi.manage.controller;

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
import com.ruoyi.manage.domain.FeExtinguisher;
import com.ruoyi.manage.service.IFeExtinguisherService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 灭火器信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/manage/extinguisher")
public class FeExtinguisherController extends BaseController
{
    @Autowired
    private IFeExtinguisherService feExtinguisherService;

    /**
     * 查询灭火器信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeExtinguisher feExtinguisher)
    {
        startPage();
        List<FeExtinguisher> list = feExtinguisherService.selectFeExtinguisherList(feExtinguisher);
        return getDataTable(list);
    }

    /**
     * 导出灭火器信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:export')")
    @Log(title = "灭火器信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeExtinguisher feExtinguisher)
    {
        List<FeExtinguisher> list = feExtinguisherService.selectFeExtinguisherList(feExtinguisher);
        ExcelUtil<FeExtinguisher> util = new ExcelUtil<FeExtinguisher>(FeExtinguisher.class);
        util.exportExcel(response, list, "灭火器信息数据");
    }

    /**
     * 获取灭火器信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:query')")
    @GetMapping(value = "/{extinguisherId}")
    public AjaxResult getInfo(@PathVariable("extinguisherId") Long extinguisherId)
    {
        return success(feExtinguisherService.selectFeExtinguisherByExtinguisherId(extinguisherId));
    }

    /**
     * 新增灭火器信息
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:add')")
    @Log(title = "灭火器信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeExtinguisher feExtinguisher)
    {
        return toAjax(feExtinguisherService.insertFeExtinguisher(feExtinguisher));
    }

    /**
     * 修改灭火器信息
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:edit')")
    @Log(title = "灭火器信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeExtinguisher feExtinguisher)
    {
        return toAjax(feExtinguisherService.updateFeExtinguisher(feExtinguisher));
    }

    /**
     * 删除灭火器信息
     */
    @PreAuthorize("@ss.hasPermi('manage:extinguisher:remove')")
    @Log(title = "灭火器信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{extinguisherIds}")
    public AjaxResult remove(@PathVariable Long[] extinguisherIds)
    {
        return toAjax(feExtinguisherService.deleteFeExtinguisherByExtinguisherIds(extinguisherIds));
    }
}
