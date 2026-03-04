package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.request.ChatEveRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


public interface ChatEveService {
    /**
     * ChatEve流式对话
     * @param emitter sseEmitter 用于向前端发送流式响应的内容
     * @return
     */
    SseEmitter chat(SseEmitter emitter, ChatEveRequest chatBody);
}
