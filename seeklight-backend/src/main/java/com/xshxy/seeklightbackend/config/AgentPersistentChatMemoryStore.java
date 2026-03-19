package com.xshxy.seeklightbackend.config;

import com.mongodb.client.result.UpdateResult;
import com.xshxy.seeklightbackend.exception.BusinessException;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

@Component
public class AgentPersistentChatMemoryStore implements ChatMemoryStore {

    public static final String COLLECTION = "agent_chat_memory_doc";
    public static final String HISTORY = "agent_chat_memory_history";

    private final MongoTemplate mongoTemplate;

    public AgentPersistentChatMemoryStore(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        if (memoryId == null) {
            throw new BusinessException("agentDialogueId为空，传入不合法");
        }
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        Map<?, ?> doc = mongoTemplate.findOne(query, Map.class, COLLECTION);
        if (doc == null) {
            return Collections.emptyList();
        }
        String json = (String) doc.get("messages");
        return messagesFromJson(json);
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String jsonMessage = messagesToJson(messages);
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        Update update = new Update().set("messages", jsonMessage);
        mongoTemplate.upsert(query, update, COLLECTION);

        ChatMessage lastMessage = messages.get(messages.size() - 1);
        Map<?, ?> history = mongoTemplate.findOne(query, Map.class, HISTORY);
        if (history == null) {
            Update historyUpdate = new Update().set("messages", jsonMessage);
            mongoTemplate.upsert(query, historyUpdate, HISTORY);
            return;
        }

        String historyJson = (String) history.get("messages");
        List<ChatMessage> historyList = messagesFromJson(historyJson);
        historyList.add(lastMessage);
        Update historyUpdate = new Update().set("messages", messagesToJson(historyList));
        mongoTemplate.upsert(query, historyUpdate, HISTORY);
    }

    @Override
    public void deleteMessages(Object memoryId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(memoryId));
        mongoTemplate.remove(query, COLLECTION);
        mongoTemplate.remove(query, HISTORY);
    }
}
