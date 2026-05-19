package com.ruoyi.manage.mapper;

import java.util.List;

import com.ruoyi.manage.domain.FeIotCard;
import com.ruoyi.manage.domain.FeIotCardMonthFlow;
import com.ruoyi.manage.domain.FeIotCardSyncLog;

public interface FeIotCardMapper
{
    FeIotCard selectFeIotCardByCardId(Long cardId);

    List<FeIotCard> selectFeIotCardList(FeIotCard feIotCard);

    List<FeIotCard> selectSyncableCards();

    int insertFeIotCard(FeIotCard feIotCard);

    int updateFeIotCard(FeIotCard feIotCard);

    int insertFromGatewaySims(FeIotCard feIotCard);

    int upsertMonthFlow(FeIotCardMonthFlow monthFlow);

    int insertSyncLog(FeIotCardSyncLog syncLog);

    int updateSyncLog(FeIotCardSyncLog syncLog);
}
