package com.ruoyi.manage.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.manage.domain.FeAiConversation;

public interface FeAiConversationMapper
{
    FeAiConversation selectFeAiConversationByConversationId(@Param("conversationId") String conversationId,
                                                            @Param("userId") Long userId);

    List<FeAiConversation> selectFeAiConversationListByUserId(@Param("userId") Long userId);

    int insertFeAiConversation(FeAiConversation conversation);

    int updateFeAiConversation(FeAiConversation conversation);

    int deleteFeAiConversationByConversationId(@Param("conversationId") String conversationId,
                                               @Param("userId") Long userId,
                                               @Param("updateBy") String updateBy);
}
