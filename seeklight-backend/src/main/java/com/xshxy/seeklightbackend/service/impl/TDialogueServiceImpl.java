package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.xshxy.seeklightbackend.service.TDialogueService;
import com.xshxy.seeklightbackend.mapper.TDialogueMapper;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;

/**
* @author 陈凯宁
* @description 针对表【t_dialogue(用户会话表)】的数据库操作Service实现
* @createDate 2025-08-30 16:19:12
*/
@Service
public class TDialogueServiceImpl extends ServiceImpl<TDialogueMapper, TDialogue>
    implements TDialogueService{

    @Resource
    private TDialogueMapper dialogueMapper;

    // MongoDB操作工具
    private final MongoTemplate mongoTemplate;

    // Jackson工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String HISTORY = "chat_memory_history";

    public TDialogueServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public List<TDialogue> getHistoryList(int userId) {
        LambdaQueryWrapper<TDialogue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TDialogue::getUserId, userId);
        queryWrapper.orderByDesc(TDialogue::getCreatedAt);
        List<TDialogue> dialogues = dialogueMapper.selectList(queryWrapper);
        return dialogues;
    }

    @Override
    public List<ChatMessage> getChatHistory(String dialogueId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(dialogueId));
        Map one = mongoTemplate.findOne(query, Map.class, HISTORY);
        if (one == null){
            return Collections.emptyList();
        }
        String historyJson = (String) one.get("messages");
        List<ChatMessage> chatHistoryList = null;
        try {
            chatHistoryList = objectMapper.readValue(historyJson, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return chatHistoryList;
    }
}




