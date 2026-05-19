package com.ruoyi.manage.service;

import java.util.List;
import java.util.Map;

import com.ruoyi.manage.domain.FeIotCard;

public interface IFeIotCardService
{
    FeIotCard selectFeIotCardByCardId(Long cardId);

    List<FeIotCard> selectFeIotCardList(FeIotCard feIotCard);

    Map<String, Object> importFromGatewaySims(String operator);

    Map<String, Object> syncAll(String operator);

    Map<String, Object> syncByCardId(Long cardId, String operator);
}
