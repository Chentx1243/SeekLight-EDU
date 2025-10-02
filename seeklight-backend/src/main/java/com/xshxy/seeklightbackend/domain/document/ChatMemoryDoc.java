package com.xshxy.seeklightbackend.domain.document;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.openai.internal.chat.Message;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "chat_memory_doc")
public class ChatMemoryDoc {
    @Id
    private String id;   // 对应 MongoDB 的 "_id"

    private List<ChatMessage> messages;

    private LocalDateTime createdAt; // 创建时间

}
