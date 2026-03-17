package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.domain.request.ChatKbRequest;
import com.xshxy.seeklightbackend.domain.resp.netIntentionResult;
import com.xshxy.seeklightbackend.manager.SseEmitterManager;
import com.xshxy.seeklightbackend.domain.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.ChatEveService;
import com.xshxy.seeklightbackend.service.IntentService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Resource
    private IntentService intentService;


    /**
     * 流式对话接口
     * @param chatBody 对话请求体
     * @return
     */

    @PostMapping("/runs")
    @Operation(summary = "流式对话", description = "基于请求体与可选API Key进行SSE对话")
    public SseEmitter chatEveRuns(
            @RequestBody ChatEveRequest chatBody){
        SseEmitter emitter = emitterManager.getEmitter(chatBody.getUser());
        return chatEveService.chat(emitter,chatBody);
    }

    /**
     * 知识库对话
     * @param chatBody 对话请求体
     * @return
     */

    @PostMapping("/kb_runs")
    @Operation(summary = "知识库：流式对话", description = "基于请求体与可选API Key进行SSE对话")
    public SseEmitter chatKnowledge(
            @RequestBody ChatKbRequest chatBody){
        SseEmitter emitter = emitterManager.getEmitter(chatBody.getUser());
        return chatEveService.chatWithKnowledgeBase(emitter,chatBody);
    }

    /**
     * 用于测试Aiservice的controller入口
     */
    @PostMapping("/test")
    public String test(@RequestBody String body){
        netIntentionResult value = intentService.intention(body);
        System.out.println("模型相应："+value.toString());
        return value.toString();
    }



}
