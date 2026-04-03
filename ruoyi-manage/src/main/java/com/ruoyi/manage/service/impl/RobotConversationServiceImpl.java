package com.ruoyi.manage.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.manage.domain.FeAiConversation;
import com.ruoyi.manage.domain.FeAiMessage;
import com.ruoyi.manage.mapper.FeAiConversationMapper;
import com.ruoyi.manage.mapper.FeAiMessageMapper;
import com.ruoyi.manage.service.IRobotConversationService;

@Service
public class RobotConversationServiceImpl implements IRobotConversationService
{
    private static final String DEL_FLAG_NORMAL = "0";
    private static final String DEL_FLAG_DELETED = "2";
    private static final int TITLE_MAX_LENGTH = 30;
    private static final int PREVIEW_MAX_LENGTH = 120;

    @Autowired
    private FeAiConversationMapper conversationMapper;

    @Autowired
    private FeAiMessageMapper messageMapper;

    @Override
    public List<FeAiConversation> selectConversationList(Long userId)
    {
        if (userId == null)
        {
            return Collections.emptyList();
        }
        return conversationMapper.selectFeAiConversationListByUserId(userId);
    }

    @Override
    public FeAiConversation selectConversation(String conversationId, Long userId)
    {
        if (isBlank(conversationId) || userId == null)
        {
            return null;
        }
        return conversationMapper.selectFeAiConversationByConversationId(conversationId, userId);
    }

    @Override
    public List<FeAiMessage> selectMessageList(String conversationId, Long userId)
    {
        if (isBlank(conversationId) || userId == null)
        {
            return Collections.emptyList();
        }
        List<FeAiMessage> messages = messageMapper.selectFeAiMessageListByConversationId(conversationId, userId);
        messages.sort(Comparator
            .comparingInt((FeAiMessage item) -> item.getSortNo() == null ? Integer.MAX_VALUE : item.getSortNo())
            .thenComparing(FeAiMessage::getCreateTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparingLong(item -> item.getMessageId() == null ? Long.MAX_VALUE : item.getMessageId()));
        return messages;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveChatHistory(Long userId, String username, String conversationId, String chatId, String question,
                                String answer, String answerType, String responseType, String status, Long elapsedMs)
    {
        if (userId == null || isBlank(conversationId) || isBlank(question))
        {
            return;
        }

        String operator = defaultIfBlank(username, "system");
        FeAiConversation existingConversation =
            conversationMapper.selectFeAiConversationByConversationId(conversationId, userId);

        int nextSortNo = defaultInt(messageMapper.selectMaxSortNoByConversationId(conversationId, userId)) + 1;

        insertMessage(userId, operator, conversationId, "user", question, "question", "text", "completed", 0L,
            nextSortNo);
        insertMessage(userId, operator, conversationId, "assistant", defaultIfBlank(answer, ""), answerType,
            responseType, defaultIfBlank(status, "completed"), defaultLong(elapsedMs), nextSortNo + 1);

        int messageCount = messageMapper.countFeAiMessageByConversationId(conversationId, userId);
        String title = existingConversation != null && !isBlank(existingConversation.getTitle())
            ? existingConversation.getTitle()
            : truncate(question, TITLE_MAX_LENGTH);

        FeAiConversation conversation = existingConversation != null ? existingConversation : new FeAiConversation();
        conversation.setConversationId(conversationId);
        conversation.setUserId(userId);
        conversation.setTitle(defaultIfBlank(title, "新建会话"));
        conversation.setLastMessagePreview(truncate(defaultIfBlank(answer, question), PREVIEW_MAX_LENGTH));
        conversation.setMessageCount(messageCount);
        conversation.setLastChatId(defaultIfBlank(chatId, ""));
        conversation.setStatus("timeout".equalsIgnoreCase(status) ? "1" : DEL_FLAG_NORMAL);
        conversation.setDelFlag(DEL_FLAG_NORMAL);
        conversation.setUpdateBy(operator);
        conversation.setUpdateTime(DateUtils.getNowDate());

        if (existingConversation == null)
        {
            conversation.setCreateBy(operator);
            conversation.setCreateTime(DateUtils.getNowDate());
            conversationMapper.insertFeAiConversation(conversation);
        }
        else
        {
            conversationMapper.updateFeAiConversation(conversation);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteConversation(String conversationId, Long userId, String username)
    {
        if (isBlank(conversationId) || userId == null)
        {
            return 0;
        }
        String operator = defaultIfBlank(username, "system");
        messageMapper.deleteFeAiMessageByConversationId(conversationId, userId, operator);
        return conversationMapper.deleteFeAiConversationByConversationId(conversationId, userId, operator);
    }

    private void insertMessage(Long userId, String username, String conversationId, String role, String content,
                               String answerType, String responseType, String status, Long elapsedMs, int sortNo)
    {
        FeAiMessage message = new FeAiMessage();
        message.setConversationId(conversationId);
        message.setUserId(userId);
        message.setRole(role);
        message.setContent(defaultIfBlank(content, ""));
        message.setAnswerType(defaultIfBlank(answerType, ""));
        message.setResponseType(defaultIfBlank(responseType, "text"));
        message.setStatus(defaultIfBlank(status, "completed"));
        message.setElapsedMs(defaultLong(elapsedMs));
        message.setSortNo(sortNo);
        message.setCreateBy(username);
        message.setCreateTime(DateUtils.getNowDate());
        message.setUpdateBy(username);
        message.setUpdateTime(DateUtils.getNowDate());
        message.setDelFlag(DEL_FLAG_NORMAL);
        messageMapper.insertFeAiMessage(message);
    }

    private int defaultInt(Integer value)
    {
        return value == null ? 0 : value;
    }

    private long defaultLong(Long value)
    {
        return value == null ? 0L : value;
    }

    private String defaultIfBlank(String value, String defaultValue)
    {
        return isBlank(value) ? defaultValue : value.trim();
    }

    private String truncate(String value, int maxLength)
    {
        if (isBlank(value))
        {
            return "";
        }
        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.length() <= maxLength)
        {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }

    private boolean isBlank(String value)
    {
        return value == null || value.trim().isEmpty();
    }
}
