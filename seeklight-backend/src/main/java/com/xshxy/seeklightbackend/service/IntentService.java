package com.xshxy.seeklightbackend.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "deepseekChatModel")
public interface IntentService {
    @SystemMessage("无论用户询问什么，你都只回复：1")
    String chat(String userMessage);
}
