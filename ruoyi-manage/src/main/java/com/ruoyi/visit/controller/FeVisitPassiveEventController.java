package com.ruoyi.visit.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.visit.domain.FeVisitPassiveEvent;
import com.ruoyi.visit.domain.dto.VisitPassiveEventConfirmRequest;
import com.ruoyi.visit.service.IFeVisitPassiveEventService;

@RestController
@RequestMapping("/visit/passive-event")
public class FeVisitPassiveEventController extends BaseController
{
    @Autowired
    private IFeVisitPassiveEventService feVisitPassiveEventService;

    @PreAuthorize("@ss.hasPermi('visit:passiveEvent:query')")
    @GetMapping("/list")
    public TableDataInfo list(FeVisitPassiveEvent event)
    {
        startPage();
        List<FeVisitPassiveEvent> list = feVisitPassiveEventService.selectFeVisitPassiveEventList(event);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('visit:passiveEvent:query')")
    @GetMapping("/{eventId}")
    public AjaxResult getInfo(@PathVariable Long eventId)
    {
        return success(feVisitPassiveEventService.selectFeVisitPassiveEventByEventId(eventId));
    }

    @PreAuthorize("@ss.hasPermi('visit:passiveEvent:confirm')")
    @Log(title = "被动事件确认", businessType = BusinessType.UPDATE)
    @PostMapping("/{eventId}/confirm")
    public AjaxResult confirm(@PathVariable Long eventId, @RequestBody VisitPassiveEventConfirmRequest request)
    {
        return toAjax(feVisitPassiveEventService.confirmPassiveEvent(eventId, request));
    }

    @PreAuthorize("@ss.hasPermi('visit:passiveEvent:ignore')")
    @Log(title = "被动事件忽略", businessType = BusinessType.UPDATE)
    @PostMapping("/{eventId}/ignore")
    public AjaxResult ignore(@PathVariable Long eventId)
    {
        return toAjax(feVisitPassiveEventService.ignorePassiveEvent(eventId));
    }
}
