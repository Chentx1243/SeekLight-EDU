package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.ChatEveService;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TUserService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ChatEveServiceImpl implements ChatEveService {

    @Resource
    private TUserService userService;

    @Resource
    private TGroupService groupService;


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
        // 构建模型流式对话组件
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl("https://api.vveai.com/v1")
                .apiKey(apiKey)
                .modelName(modelCode)
                .build();
        // 发起对话请求
       model.chat(userContent, new StreamingChatResponseHandler() {
           @Override
           public void onPartialResponse(String s) {
               try {
                   emitter.send(s);
               } catch (IOException e) {
                   throw new RuntimeException(e);
               }
           }

           @Override
           public void onCompleteResponse(ChatResponse chatResponse) {
               System.out.printf("complete");
           }

           @Override
           public void onError(Throwable throwable) {
                System.out.printf("error");
           }
       });
        return emitter;
    }
}
