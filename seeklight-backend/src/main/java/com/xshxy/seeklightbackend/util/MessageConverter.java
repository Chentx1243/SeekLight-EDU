package com.xshxy.seeklightbackend.util;

import com.xshxy.seeklightbackend.domain.dto.MessageDTO;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.UserMessage;
import lombok.Data;

import java.util.List;

/**
 * 将langchain4j消息转化为DTO类型转化器
 */
@Data
public class MessageConverter {

    /**
     * 转化单个chatMessage为dto
     * @param mes 原始对话任意实现类
     * @return dto实例
     */
    public static MessageDTO converterMessage(ChatMessage mes){
        MessageDTO dto = new MessageDTO();
        // 根据不同类型转化为dto
        if (mes instanceof AiMessage aiMessage){
            dto.setRole("AI");
            dto.setContent(aiMessage.text());
        }else if (mes instanceof UserMessage userMessage){
            dto.setRole("USER");
            dto.setContent(userMessage.singleText());
        }
        return dto;
    }

    /**
     * 转化List中的chatMessage为dto
     * @param mes 原始对话集合
     * @return dto对话集合
     */
    public static List<MessageDTO> converterMessageList(List<ChatMessage> mes){
        return mes.stream().map(MessageConverter::converterMessage).toList();

    }

}
