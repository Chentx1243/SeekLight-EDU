package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.resp.netIntentionResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "deepseekChatModel")
public interface IntentService {
    @SystemMessage("识别用户当前提问是否是时效性较强,分析是否启动联网搜索以及联网搜索的关键词（关键词中不要包含时间）")
    netIntentionResult intention(String userMessage);
}
