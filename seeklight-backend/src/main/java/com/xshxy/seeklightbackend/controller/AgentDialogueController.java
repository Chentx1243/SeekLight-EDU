package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TAgentDialogue;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.dto.MessageDTO;
import com.xshxy.seeklightbackend.domain.request.InitAgentDialogueRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TAgentDialogueService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Agent对话接口")
@RestController
@RequestMapping("/agent/dialogue")
public class AgentDialogueController {

    @Resource
    private TAgentDialogueService agentDialogueService;

    @Resource
    private UserInfoService userInfoService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping("/init")
    @Operation(summary = "初始化Agent会话", description = "为当前登录用户创建指定Agent的新会话")
    public Long initDialogue(@RequestBody InitAgentDialogueRequest request) {
        TUser user = requireCurrentUser();
        return agentDialogueService.initDialogue(request.getAgentId(), user.getUserId(), user.getGroupId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/history")
    @Operation(summary = "查询Agent会话列表", description = "获取当前登录用户的Agent会话历史")
    public List<TAgentDialogue> getHistoryList() {
        TUser user = requireCurrentUser();
        return agentDialogueService.getHistoryList(user.getUserId());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/history")
    @Operation(summary = "删除Agent会话", description = "删除指定Agent会话及其历史记录")
    public Result<String> deleteHistoryItem(
            @Parameter(description = "Agent会话ID", required = true)
            @RequestParam("agentDialogueId") Long agentDialogueId) {
        TUser user = requireCurrentUser();
        agentDialogueService.getOwnedDialogue(agentDialogueId, user.getUserId());
        return agentDialogueService.deleteHistoryItem(agentDialogueId);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/chatHistory")
    @Operation(summary = "查询Agent会话详情", description = "根据Agent会话ID获取消息列表")
    public Result<List<MessageDTO>> getChatHistory(
            @Parameter(description = "Agent会话ID", required = true)
            @RequestParam("agentDialogueId") Long agentDialogueId) {
        TUser user = requireCurrentUser();
        agentDialogueService.getOwnedDialogue(agentDialogueId, user.getUserId());
        return Result.success(agentDialogueService.getChatHistory(agentDialogueId));
    }

    private TUser requireCurrentUser() {
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        return user;
    }
}
