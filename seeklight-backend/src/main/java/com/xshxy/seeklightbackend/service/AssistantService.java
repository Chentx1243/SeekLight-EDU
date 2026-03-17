package com.xshxy.seeklightbackend.service;

import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * 用于管理对话接口
 */
public interface AssistantService{


    TokenStream chat(@MemoryId Long memoryId, @UserMessage String message);

    @SystemMessage("""
            你是一个知识库问答助理。
            请基于检索到的知识库内容回答问题。
            要求：
            1. 优先依据检索结果回答；
            2. 不要编造知识库中不存在的事实；
            3. 如果检索结果不足，请明确说明信息不足；
            4. 回答尽量清晰、准确、工程化。
            """)
    TokenStream chatKnowledgeBase(@MemoryId Long memoryId,
                     @UserMessage String userMessage,
                     InvocationParameters invocationParameters);

}
