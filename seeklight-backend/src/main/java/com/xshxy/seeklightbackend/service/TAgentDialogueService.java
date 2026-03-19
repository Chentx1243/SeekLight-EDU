package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TAgentDialogue;
import com.xshxy.seeklightbackend.domain.dto.MessageDTO;

import java.util.List;

public interface TAgentDialogueService extends IService<TAgentDialogue> {

    Long initDialogue(Long agentId, Integer userId, Integer groupId);

    List<TAgentDialogue> getHistoryList(Integer userId);

    Result<String> deleteHistoryItem(Long agentDialogueId);

    List<MessageDTO> getChatHistory(Long agentDialogueId);

    TAgentDialogue getOwnedDialogue(Long agentDialogueId, Integer userId);
}
