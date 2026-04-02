package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeAiMessage;

public interface FeAiMessageMapper
{
    List<FeAiMessage> selectFeAiMessageListByConversationId(@Param("conversationId") String conversationId,
                                                            @Param("userId") Long userId);

    int insertFeAiMessage(FeAiMessage message);

    Integer selectMaxSortNoByConversationId(@Param("conversationId") String conversationId,
                                            @Param("userId") Long userId);

    int countFeAiMessageByConversationId(@Param("conversationId") String conversationId,
                                         @Param("userId") Long userId);

    int deleteFeAiMessageByConversationId(@Param("conversationId") String conversationId,
                                          @Param("userId") Long userId,
                                          @Param("updateBy") String updateBy);
}
