package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.baomidou.mybatisplus.extension.service.IService;
import dev.langchain4j.data.message.ChatMessage;

import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_dialogue(用户会话表)】的数据库操作Service
* @createDate 2025-08-30 16:19:12
*/
public interface TDialogueService extends IService<TDialogue> {

    /**
     * 根据userId获取该用户的dialogue
     * @param userId 用户id
     * @return
     */
    List<TDialogue> getHistoryList(int userId);

    /**
     * 根据对话id获取与ai对话的详细内容
     * @param dialogueId 对话id
     * @return 对话列表
     */
    List<ChatMessage> getChatHistory(Long dialogueId);

    /**
     * 根据对话id删除对话
     * @param dialogueId 要删除的对话id
     * @return 删除情况
     */
    Result<String> deleteHistoryItem(Long dialogueId);

    /**
     * 根据对话id查询对话
     * @param dialogueId 对话id
     * @return 对话信息
     */
    TDialogue getById(Long dialogueId);
}
