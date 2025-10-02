package com.xshxy.seeklightbackend.service;

import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.ChatMemoryAccess;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 用于管理对话接口
 */
public interface AssistantService extends ChatMemoryAccess {


    TokenStream chat(@MemoryId String memoryId, @UserMessage String message);

}
