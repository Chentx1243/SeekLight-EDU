package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.manager.SseEmitterManager;
import com.xshxy.seeklightbackend.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.ChatEveService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    public SseEmitter chatEveRuns(
            @RequestBody ChatEveRequest chatBody,
            @RequestHeader(value = "Api-key", required = false) String key){
        SseEmitter emitter = emitterManager.getEmitter(chatBody.getUser());
        return chatEveService.chat(emitter,chatBody,key);
    }



}
