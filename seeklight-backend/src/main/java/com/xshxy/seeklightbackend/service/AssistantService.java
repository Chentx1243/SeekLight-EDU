package com.xshxy.seeklightbackend.service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 用于管理对话接口
 */
public interface AssistantService {

    /**
     * 附带记忆的对话接口
     * @param memoryId 用户记忆唯一标识
     * @param userMessage 用户的新消息
     * @return 流式响应
     */
    SseEmitter chat(@MemoryId int memoryId, @UserMessage String userMessage);

}
