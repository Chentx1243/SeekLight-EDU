package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.domain.request.AgentChatRequest;
import com.xshxy.seeklightbackend.manager.SseEmitterManager;
import com.xshxy.seeklightbackend.service.AgentChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "Agent聊天接口")
@RestController
@RequestMapping("/agent/chat")
public class AgentChatController {

    @Resource
    private SseEmitterManager emitterManager;

    @Resource
    private AgentChatService agentChatService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/runs")
    @Operation(summary = "Agent流式对话", description = "基于FastGPT的Agent独立对话接口，由平台侧管理上下文与历史")
    public SseEmitter chat(@RequestBody AgentChatRequest request) {
        SseEmitter emitter = emitterManager.getEmitter(request.getUser());
        return agentChatService.chat(emitter, request);
    }
}
