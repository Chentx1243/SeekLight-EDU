package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.config.AgentPersistentChatMemoryStore;
import com.xshxy.seeklightbackend.config.FastGptProperties;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentDialogue;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.request.AgentChatRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.AgentChatService;
import com.xshxy.seeklightbackend.service.TAgentDialogueService;
import com.xshxy.seeklightbackend.service.TAgentGroupPermissionService;
import com.xshxy.seeklightbackend.service.TAgentService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import com.xshxy.seeklightbackend.util.AgentDialogueMemoryUtil;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class AgentChatServiceImpl implements AgentChatService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TAgentDialogueService agentDialogueService;

    @Resource
    private TAgentService agentService;

    @Resource
    private TAgentGroupPermissionService agentGroupPermissionService;

    @Resource
    private AgentPersistentChatMemoryStore agentChatMemoryStore;

    @Resource
    private FastGptProperties fastGptProperties;

    @Override
    public SseEmitter chat(SseEmitter emitter, AgentChatRequest request) {
        TUser user = requireCurrentUser();
        TAgentDialogue dialogue = agentDialogueService.getOwnedDialogue(request.getAgentDialogueId(), user.getUserId());
        TAgent agent = getAccessibleAgent(dialogue.getAgentId(), user);
        String userContent = extractLatestUserMessage(request);
        updateDialogueTitle(dialogue, userContent);

        OpenAiStreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .baseUrl(getFastGptBaseUrl())
                .apiKey(agent.getAppKey())
                .modelName(resolveModelName(agent))
                .defaultRequestParameters(buildRequestParameters(agent))
                .build();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(agentChatMemoryStore)
                .maxMessages(fastGptProperties.getMaxMessages())
                .build();

        AgentAssistantService assistantService = AiServices.builder(AgentAssistantService.class)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        String memoryId = AgentDialogueMemoryUtil.memoryId(dialogue.getAgentDialogueId());
        TokenStream tokenStream = assistantService.chat(memoryId, userContent);
        tokenStream
                .onPartialResponse(partialResponse -> {
                    try {
                        emitter.send(partialResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(response -> {
                    log.info("Agent对话完成，agentDialogueId={}", request.getAgentDialogueId());
                    dialogue.setUpdatedAt(new java.util.Date());
                    agentDialogueService.updateById(dialogue);
                    emitter.complete();
                })
                .onError(error -> {
                    log.error("Agent对话失败，agentDialogueId={}", request.getAgentDialogueId(), error);
                    emitter.completeWithError(error);
                })
                .start();
        return emitter;
    }

    private TUser requireCurrentUser() {
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        return user;
    }

    private TAgent getAccessibleAgent(Long agentId, TUser user) {
        if (agentId == null) {
            throw new BusinessException("Agent ID不能为空");
        }
        TAgent agent = agentService.getById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在");
        }
        if (!Integer.valueOf(1).equals(agent.getStatus())) {
            throw new BusinessException("当前Agent已禁用");
        }
        if (user.getUserId().equals(agent.getOwnerUserId())) {
            return agent;
        }
        if (Integer.valueOf(1).equals(agent.getVisibility())) {
            LambdaQueryWrapper<TAgentGroupPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(TAgentGroupPermission::getAgentId, agentId)
                    .eq(TAgentGroupPermission::getGroupId, user.getGroupId());
            TAgentGroupPermission permission = agentGroupPermissionService.getOne(wrapper);
            if (permission != null) {
                return agent;
            }
        }
        throw new BusinessException("当前用户无权使用该Agent");
    }

    private String extractLatestUserMessage(AgentChatRequest request) {
        if (request == null || request.getAgentDialogueId() == null) {
            throw new BusinessException("会话ID不能为空");
        }
        if (request.getMessages() == null || request.getMessages().isEmpty()) {
            throw new BusinessException("聊天消息不能为空");
        }
        for (int i = request.getMessages().size() - 1; i >= 0; i--) {
            AgentChatRequest.Message message = request.getMessages().get(i);
            if (message != null
                    && "user".equalsIgnoreCase(message.getRole())
                    && StringUtils.hasText(message.getContent())) {
                return message.getContent().trim();
            }
        }
        throw new BusinessException("必须包含一条用户消息");
    }

    private void updateDialogueTitle(TAgentDialogue dialogue, String userContent) {
        if (dialogue.getTitle() == null || dialogue.getTitle().isBlank()) {
            dialogue.setTitle(userContent.substring(0, Math.min(10, userContent.length())));
            agentDialogueService.updateById(dialogue);
        }
    }

    private String getFastGptBaseUrl() {
        if (!StringUtils.hasText(fastGptProperties.getBaseUrl())) {
            throw new BusinessException("缺少FastGPT baseUrl配置，请设置 agent.fastgpt.base-url");
        }
        return fastGptProperties.getBaseUrl().trim();
    }

    private String resolveModelName(TAgent agent) {
        if (StringUtils.hasText(agent.getAppId())) {
            return agent.getAppId().trim();
        }
        return "fastgpt-agent";
    }

    private OpenAiChatRequestParameters buildRequestParameters(TAgent agent) {
        String appId = agent.getAppId();
        if (!StringUtils.hasText(appId)) {
            return OpenAiChatRequestParameters.builder()
                    .modelName(resolveModelName(agent))
                    .build();
        }
        return OpenAiChatRequestParameters.builder()
                .modelName(resolveModelName(agent))
                .customParameters(Map.of("appId", appId.trim()))
                .build();
    }
}
