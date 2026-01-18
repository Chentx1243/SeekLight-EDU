package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.manager.SseEmitterManager;
import com.xshxy.seeklightbackend.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.ChatEveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "聊天接口")
@RestController
@RequestMapping("/chatEve")
public class ChatEveController {

    @Resource
    private SseEmitterManager emitterManager;

    @Resource
    private ChatEveService chatEveService;

    /**
     * 流式对话接口
     * @param chatBody 对话请求体
     * @param key 可选：V3平台-APIkey
     * @return
     */

    @PostMapping("/runs")
    @Operation(summary = "流式对话", description = "基于请求体与可选API Key进行SSE对话")
    public SseEmitter chatEveRuns(
            @RequestBody ChatEveRequest chatBody,
            @Parameter(description = "可选：平台API Key")
            @RequestHeader(value = "Api-key", required = false) String key){
        SseEmitter emitter = emitterManager.getEmitter(chatBody.getUser());
        return chatEveService.chat(emitter,chatBody,key);
    }



}
