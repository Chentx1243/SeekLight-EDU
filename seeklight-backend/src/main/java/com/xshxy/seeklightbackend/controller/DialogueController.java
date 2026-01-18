package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TDialogueService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import dev.langchain4j.data.message.ChatMessage;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dialogue")
public class DialogueController {

    @Resource
    private TDialogueService dialogueService;

    @Resource
    private UserInfoService userInfoService;

    /**
     * 初始化对话id
     * @return 对话id
     */
    @PostMapping("/init")
    public Long initDialogue() {
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        TDialogue dialogue = new TDialogue();
        dialogue.setUserId(user.getUserId());
        dialogue.setModelId(1);
        dialogue.setTitle("");
        dialogueService.save(dialogue);
        return dialogue.getDialogueId();
    }


    /**
     * 获取dialogue列表
     * @param userId 用户Id
     * @return 对话列表
     */
    @GetMapping("/history")
    public List<TDialogue> getHistoryList(){
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            return List.of();
        }
        List<TDialogue> dialoguesList = dialogueService.getHistoryList(user.getUserId());
        return dialoguesList;
    }

    /**
     * 删除dialogue
     * @param dialogueId
     * @return
     */
    @DeleteMapping("/history")
    public Result<String> deleteHistoryItem(@RequestParam("dialogueId") Long dialogueId){
        // 获取当前登录用户
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            return Result.failure("用户未登录");
        }

        // 查询对话信息并校验权限
        TDialogue dialogue = dialogueService.getById(dialogueId);
        if (dialogue == null) {
            return Result.failure("对话不存在");
        }

        // 校验对话是否属于当前用户
        if (!dialogue.getUserId().equals(user.getUserId())) {
            return Result.failure("无权删除该对话");
        }

        // 执行删除操作
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
        // 获取当前登录用户
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            return List.of();
        }

        // 查询对话信息并校验权限
        TDialogue dialogue = dialogueService.getById(dialogueId);
        if (dialogue == null) {
            return List.of();
        }

        // 校验对话是否属于当前用户
        if (!dialogue.getUserId().equals(user.getUserId())) {
            return List.of();
        }

        // 执行查询操作
        return dialogueService.getChatHistory(dialogueId);
    }


}
