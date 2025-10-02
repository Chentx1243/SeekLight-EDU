package com.xshxy.seeklightbackend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.xshxy.seeklightbackend.domain.document.ChatMemoryDoc;
import com.xshxy.seeklightbackend.exception.BusinessException;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.openai.internal.chat.Message;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
@Slf4j
public class PersistentChatMemoryStore implements ChatMemoryStore {

    // MongoDB操作工具
    private final MongoTemplate mongoTemplate;

    // Jackson工具
    private final ObjectMapper objectMapper = new ObjectMapper();

    // 文档字段
    private final String COLLECTION = "chat_memory_doc";
    private final String HISTORY = "chat_memory_history";


    public PersistentChatMemoryStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        // 判空逻辑
        if(memoryId == null){
            throw new BusinessException("dialogueId为空，传入不合法");
        }
        // mongodb中查询聊天记录
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        Map doc = mongoTemplate.findOne(query, Map.class, COLLECTION);
        // 判断是否查到对话记录(首次发起对话是，doc为空)
        if (doc == null){
            return Collections.emptyList();
        }
        // 非首次发起对话，存入已有的上下文信息；
        String json = (String) doc.get("messages");
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        // 将最新的上下文，转化为json
        String jsonMessage = messagesToJson(list);
        // 写入mongoDb中
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        Update update = new Update().set("messages", jsonMessage);
        UpdateResult updateResult = mongoTemplate.upsert(query, update, COLLECTION);
        if (updateResult.getUpsertedId() != null) {
            System.out.println("已追加记录" + memoryId);
        }

        // 持久存储对话历史
        ChatMessage lastMessage = list.get(list.size() - 1);
        // 获取该对话的历史
        // TODO 使用redis优化（待）
        Map history = mongoTemplate.findOne(query, Map.class,HISTORY);
        // 第一次对话
        if (history == null){
            history = mongoTemplate.findOne(query, Map.class,COLLECTION);
            String historyJson = (String) history.get("messages");
            // 将historyList存入历史记录表
            Update historyUpdate = new Update().set("messages", historyJson);
            UpdateResult historyUpdateResult = mongoTemplate.upsert(query, historyUpdate, HISTORY);
            if (historyUpdateResult.getUpsertedId() != null) {
                System.out.println("历史记录已保存" + memoryId);
            }
            return;
        }

        String historyJson = (String) history.get("messages");
        List<ChatMessage> historyList = messagesFromJson(historyJson);
        historyList.add(lastMessage);
        String jasonList = messagesToJson(historyList);
        // 将historyList存入历史记录表
        Update historyUpdate = new Update().set("messages", jasonList);
        UpdateResult historyUpdateResult = mongoTemplate.upsert(query, historyUpdate, HISTORY);
        if (historyUpdateResult.getUpsertedId() != null) {
            System.out.println("历史记录已保存" + memoryId);
        }


    }

    @Override
    public void deleteMessages(Object o) {
        System.out.println("deleteMessages");

    }
}
