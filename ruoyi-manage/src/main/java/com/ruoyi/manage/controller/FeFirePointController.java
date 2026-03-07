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
import com.ruoyi.manage.domain.FeFirePoint;
import com.ruoyi.manage.service.IFeFirePointService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 消防点信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-03
 */
@RestController
@RequestMapping("/manage/point")
public class FeFirePointController extends BaseController
{
    @Autowired
    private IFeFirePointService feFirePointService;

    /**
     * 查询消防点信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:point:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeFirePoint feFirePoint)
    {
        startPage();
        List<FeFirePoint> list = feFirePointService.selectFeFirePointList(feFirePoint);
        return getDataTable(list);
    }

    /**
     * 导出消防点信息列表
     */
    @PreAuthorize("@ss.hasPermi('manage:point:export')")
    @Log(title = "消防点信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, FeFirePoint feFirePoint)
    {
        List<FeFirePoint> list = feFirePointService.selectFeFirePointList(feFirePoint);
        ExcelUtil<FeFirePoint> util = new ExcelUtil<FeFirePoint>(FeFirePoint.class);
        util.exportExcel(response, list, "消防点信息数据");
    }

    /**
     * 获取消防点信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('manage:point:query')")
    @GetMapping(value = "/{firePointId}")
    public AjaxResult getInfo(@PathVariable("firePointId") Long firePointId)
    {
        return success(feFirePointService.selectFeFirePointByFirePointId(firePointId));
    }

    /**
     * 新增消防点信息
     */
    @PreAuthorize("@ss.hasPermi('manage:point:add')")
    @Log(title = "消防点信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody FeFirePoint feFirePoint)
    {
        return toAjax(feFirePointService.insertFeFirePoint(feFirePoint));
    }

    /**
     * 修改消防点信息
     */
    @PreAuthorize("@ss.hasPermi('manage:point:edit')")
    @Log(title = "消防点信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody FeFirePoint feFirePoint)
    {
        return toAjax(feFirePointService.updateFeFirePoint(feFirePoint));
    }

    /**
     * 删除消防点信息
     */
    @PreAuthorize("@ss.hasPermi('manage:point:remove')")
    @Log(title = "消防点信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{firePointIds}")
    public AjaxResult remove(@PathVariable Long[] firePointIds)
    {
        return toAjax(feFirePointService.deleteFeFirePointByFirePointIds(firePointIds));
    }
}
