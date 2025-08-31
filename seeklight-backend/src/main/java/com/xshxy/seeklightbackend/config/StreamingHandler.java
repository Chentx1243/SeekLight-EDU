package com.xshxy.seeklightbackend.config;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StreamingHandler {
    public StreamingChatResponseHandler eveChatStreamingHandler(){

        return new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(String s) {
                // 在这里可以将每次返回的token按照特定方式返回给前端
            }

            @Override
            public void onCompleteResponse(ChatResponse chatResponse) {
                // 在这里可以做聊天记录的保存
            }

            @Override
            public void onError(Throwable throwable) {
                // 在这里可以做异常处理
            }
        };
    }
}
