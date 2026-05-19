package com.ruoyi.manage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.manage.domain.FeIotCard;
import com.ruoyi.manage.service.IFeIotCardService;

@RestController
@RequestMapping("/manage/iot-card")
public class FeIotCardController extends BaseController
{
    @Autowired
    private IFeIotCardService feIotCardService;

    @PreAuthorize("@ss.hasPermi('manage:iotCard:list')")
    @GetMapping("/list")
    public TableDataInfo list(FeIotCard feIotCard)
    {
        startPage();
        List<FeIotCard> list = feIotCardService.selectFeIotCardList(feIotCard);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('manage:iotCard:query')")
    @GetMapping("/{cardId}")
    public AjaxResult getInfo(@PathVariable Long cardId)
    {
        return success(feIotCardService.selectFeIotCardByCardId(cardId));
    }

    @PreAuthorize("@ss.hasPermi('manage:iotCard:sync')")
    @PostMapping("/seed-from-gateway")
    public AjaxResult seedFromGateway()
    {
        Map<String, Object> result = feIotCardService.importFromGatewaySims(getUsername());
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('manage:iotCard:sync')")
    @PostMapping("/sync")
    public AjaxResult syncAll()
    {
        Map<String, Object> result = feIotCardService.syncAll(getUsername());
        return AjaxResult.success(result);
    }

    @PreAuthorize("@ss.hasPermi('manage:iotCard:sync')")
    @PostMapping("/{cardId}/sync")
    public AjaxResult syncOne(@PathVariable Long cardId)
    {
        Map<String, Object> result = feIotCardService.syncByCardId(cardId, getUsername());
        return AjaxResult.success(result);
    }
}
