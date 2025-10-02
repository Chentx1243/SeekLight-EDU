package com.xshxy.seeklightbackend.controller;

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

    /**
     * 获取历史对话列表
     * @param userId 用户Id
     * @return 对话列表
     */
    @GetMapping("/history")
    public List<TDialogue> getHistoryList(@RequestParam("userId") int userId){
        List<TDialogue> dialoguesList = dialogueService.getHistoryList(userId);
        return dialoguesList;
    }


    /**
     * 根据对话id获取该对话的历史数据
     * @param dialogueId 对话id
     * @return 对话列表
     */

    @GetMapping("/chatHistory")
    public List<ChatMessage> getChatHistory(@RequestParam("dialogueId") String dialogueId){
        List<ChatMessage> result  = dialogueService.getChatHistory(dialogueId);
        return result;
    }


}
