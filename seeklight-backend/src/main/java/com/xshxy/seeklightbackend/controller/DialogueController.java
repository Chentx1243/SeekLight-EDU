package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.xshxy.seeklightbackend.service.TDialogueService;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dialogue")
public class DialogueController {

    @Resource
    private TDialogueService dialogueService;


    /**
     * 获取dialogue列表
     * @param userId 用户Id
     * @return 对话列表
     */
    @GetMapping("/history")
    public List<TDialogue> getHistoryList(@RequestParam("userId") int userId){
        List<TDialogue> dialoguesList = dialogueService.getHistoryList(userId);
        return dialoguesList;
    }

    /**
     * 删除dialogue
     * @param dialogueId
     * @return
     */
    @DeleteMapping("/history")
    public Result<String> deleteHistoryItem(@RequestParam("dialogueId") Long dialogueId){
        Result<String> result = dialogueService.deleteHistoryItem(dialogueId);
        return result;
    }

    /**
     * 根据对话id获取该对话的历史数据
     * @param dialogueId 对话id
     * @return 对话列表
     */

    @GetMapping("/chatHistory")
    public List<ChatMessage> getChatHistory(@RequestParam("dialogueId") Long dialogueId){
        List<ChatMessage> result  = dialogueService.getChatHistory(dialogueId);
        return result;
    }


}
