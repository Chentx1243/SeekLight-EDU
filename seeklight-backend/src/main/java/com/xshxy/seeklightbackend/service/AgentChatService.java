package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.request.AgentChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AgentChatService {
    /**
     * Agent对话的流式接口（对接FastGPT）
     * @param emitter
     * @param request
     * @return
     */
    SseEmitter chat(SseEmitter emitter, AgentChatRequest request);
}
