package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mongodb.client.result.DeleteResult;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentDialogue;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.dto.MessageDTO;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TAgentGroupPermissionMapper;
import com.xshxy.seeklightbackend.mapper.TAgentDialogueMapper;
import com.xshxy.seeklightbackend.mapper.TAgentMapper;
import com.xshxy.seeklightbackend.service.TAgentDialogueService;
import com.xshxy.seeklightbackend.util.AgentDialogueMemoryUtil;
import com.xshxy.seeklightbackend.util.MessageConverter;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;

@Service
public class TAgentDialogueServiceImpl extends ServiceImpl<TAgentDialogueMapper, TAgentDialogue>
        implements TAgentDialogueService {

    private static final String COLLECTION = "chat_memory_doc";
    private static final String HISTORY = "chat_memory_history";

    private final MongoTemplate mongoTemplate;
    private final TAgentMapper agentMapper;
    private final TAgentGroupPermissionMapper agentGroupPermissionMapper;

    public TAgentDialogueServiceImpl(MongoTemplate mongoTemplate,
                                    TAgentMapper agentMapper,
                                    TAgentGroupPermissionMapper agentGroupPermissionMapper) {
        this.mongoTemplate = mongoTemplate;
        this.agentMapper = agentMapper;
        this.agentGroupPermissionMapper = agentGroupPermissionMapper;
    }

    @Override
    public Long initDialogue(Long agentId, Integer userId, Integer groupId) {
        if (agentId == null) {
            throw new BusinessException("Agent ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }
        TAgent agent = agentMapper.selectById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在");
        }
        if (!Integer.valueOf(1).equals(agent.getStatus())) {
            throw new BusinessException("当前Agent已禁用");
        }
        if (!userId.equals(agent.getOwnerUserId())) {
            if (!Integer.valueOf(1).equals(agent.getVisibility()) || groupId == null) {
                throw new BusinessException("当前用户无权使用该Agent");
            }
            LambdaQueryWrapper<TAgentGroupPermission> permissionWrapper = new LambdaQueryWrapper<>();
            permissionWrapper.eq(TAgentGroupPermission::getAgentId, agentId)
                    .eq(TAgentGroupPermission::getGroupId, groupId);
            TAgentGroupPermission permission = agentGroupPermissionMapper.selectOne(permissionWrapper);
            if (permission == null) {
                throw new BusinessException("当前用户无权使用该Agent");
            }
        }

        Date now = new Date();
        TAgentDialogue dialogue = new TAgentDialogue();
        dialogue.setAgentId(agentId);
        dialogue.setUserId(userId);
        dialogue.setTitle("");
        dialogue.setStatus(1);
        dialogue.setIsDeleted(0);
        dialogue.setCreatedAt(now);
        dialogue.setUpdatedAt(now);
        save(dialogue);
        return dialogue.getAgentDialogueId();
    }

    @Override
    public List<TAgentDialogue> getHistoryList(Integer userId) {
        LambdaQueryWrapper<TAgentDialogue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TAgentDialogue::getUserId, userId);
        queryWrapper.orderByDesc(TAgentDialogue::getUpdatedAt);
        return baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> deleteHistoryItem(Long agentDialogueId) {
        LambdaQueryWrapper<TAgentDialogue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TAgentDialogue::getAgentDialogueId, agentDialogueId);
        int deleteCount = baseMapper.delete(queryWrapper);

        String memoryId = AgentDialogueMemoryUtil.memoryId(agentDialogueId);
        Query query = new Query(Criteria.where("_id").is(memoryId));
        DeleteResult removeHistory = mongoTemplate.remove(query, HISTORY);
        DeleteResult removeMemory = mongoTemplate.remove(query, COLLECTION);

        if (deleteCount > 0 || removeHistory.getDeletedCount() > 0 || removeMemory.getDeletedCount() > 0) {
            return Result.success("删除成功");
        }
        return Result.failure("未找到可删除的会话");
    }

    @Override
    public List<MessageDTO> getChatHistory(Long agentDialogueId) {
        String memoryId = AgentDialogueMemoryUtil.memoryId(agentDialogueId);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        Map<?, ?> history = mongoTemplate.findOne(query, Map.class, HISTORY);
        if (history == null) {
            return Collections.emptyList();
        }

        String historyJson = (String) history.get("messages");
        try {
            List<ChatMessage> chatHistoryList = messagesFromJson(historyJson);
            chatHistoryList.removeIf(chatMessage -> chatMessage instanceof SystemMessage);
            return MessageConverter.converterMessageList(chatHistoryList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TAgentDialogue getOwnedDialogue(Long agentDialogueId, Integer userId) {
        if (agentDialogueId == null) {
            throw new BusinessException("会话ID不能为空");
        }
        if (userId == null) {
            throw new BusinessException("用户未登录");
        }

        LambdaQueryWrapper<TAgentDialogue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TAgentDialogue::getAgentDialogueId, agentDialogueId)
                .eq(TAgentDialogue::getUserId, userId);
        TAgentDialogue dialogue = baseMapper.selectOne(queryWrapper);
        if (dialogue == null) {
            throw new BusinessException("会话不存在或无权访问");
        }
        return dialogue;
    }
}
