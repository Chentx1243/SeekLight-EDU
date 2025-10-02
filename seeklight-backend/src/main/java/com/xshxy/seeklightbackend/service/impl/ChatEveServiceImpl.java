package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.config.PersistentChatMemoryStore;
import com.xshxy.seeklightbackend.domain.TDialogue;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.*;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class ChatEveServiceImpl implements ChatEveService {

    @Resource
    private TUserService userService;

    @Resource
    private TGroupService groupService;

    @Resource
    private TDialogueService dialogueService;

    @Resource
    private TModelService modelService;

    @Resource
    private PersistentChatMemoryStore store;

    @Override
    public SseEmitter chat(SseEmitter emitter, ChatEveRequest chatBody, String key) {
        // 获取用户信息
        TUser user = userService.getById(chatBody.getUser());
        // 获取分组
        TGroup group = groupService.getById(user.getGroupId());
        // 获取请求的模型
        String modelCode = chatBody.getModel();
        String apiKey = group.getGroupApiKey();
        // 获取用户的提问
        List<ChatEveRequest.Message> messages = chatBody.getMessages();
        ChatEveRequest.Message message = messages.get(0);
        String userContent = message.getContent();
        // 获取dialogueId(string转为int)
        int dialogueId = Integer.parseInt(chatBody.getDialogueId());
        // 构建模型组件（真正用于对话的模型核心）
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("https://api.vveai.com/v1")
                .apiKey(apiKey)
                .modelName(modelCode)
                .build();

        // 记忆组件
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(store)
                .maxMessages(5)
                .build();

        // 构建Ai服务
        AssistantService aiService = AiServices.builder(AssistantService.class)
                .streamingChatLanguageModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        // 判断是否第一次发起对话
        // TODO 优化标题记录方式
        TDialogue dialogue = dialogueService.getById(dialogueId);
        if (dialogue == null){
            dialogue = new TDialogue();
            dialogue.setModelId(1);
            dialogue.setUserId(user.getUserId());
            dialogue.setDialogueId((long) dialogueId);
            // 最多截取10个用户输入的内容作为title
            dialogue.setTitle(userContent.substring(0,Math.min(10,userContent.length())));
            // 存入数据库
            dialogueService.save(dialogue);
        }

        // 利用Ai服务发起对话请求
        String memoryId = chatBody.getDialogueId();
        TokenStream tokenRespond = aiService.chat(memoryId,userContent);
        // 注册流式行为
        tokenRespond.onPartialResponse((String partialResponse) -> {
                    try {
                        emitter.send(partialResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse((ChatResponse response) -> {
                    System.out.println("响应完成，完整结果如下：");
                    System.out.println(response);
                    // 结束响应
                    emitter.complete();

                })
                .onError((Throwable error) -> error.printStackTrace())
                .start();


        return emitter;
    }
}
