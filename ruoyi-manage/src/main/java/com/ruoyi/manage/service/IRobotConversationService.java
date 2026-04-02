package com.ruoyi.manage.service;

import java.util.List;
import com.ruoyi.manage.domain.FeAiConversation;
import com.ruoyi.manage.domain.FeAiMessage;

public interface IRobotConversationService
{
    List<FeAiConversation> selectConversationList(Long userId);

    FeAiConversation selectConversation(String conversationId, Long userId);

    List<FeAiMessage> selectMessageList(String conversationId, Long userId);

    void saveChatHistory(Long userId, String username, String conversationId, String chatId, String question,
                         String answer, String answerType, String responseType, String status, Long elapsedMs);

    int deleteConversation(String conversationId, Long userId, String username);
}
