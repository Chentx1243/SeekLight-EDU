package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.request.AgentChatRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface AgentChatService {

    SseEmitter chat(SseEmitter emitter, AgentChatRequest request);
}
