package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.manager.SseEmitterManager;
import com.xshxy.seeklightbackend.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.ChatEveService;
import com.xshxy.seeklightbackend.service.TDialogueService;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/chatEve")
public class ChatEveController {

    @Resource
    private SseEmitterManager emitterManager;

    @Resource
    private ChatEveService chatEveService;

    @Resource
    private TDialogueService dialogueService;

    /**
     * 流式对话接口
     * @param chatBody 对话请求体
     * @param key 可选：V3平台-APIkey
     * @return
     */

    @PostMapping("/runs")
    public SseEmitter chatEveRuns(
            @RequestBody ChatEveRequest chatBody,
            @RequestHeader(value = "Authorization", required = false) String key){
        SseEmitter emitter = emitterManager.getEmitter(chatBody.getUser());
        return chatEveService.chat(emitter,chatBody,key);
    }



}
