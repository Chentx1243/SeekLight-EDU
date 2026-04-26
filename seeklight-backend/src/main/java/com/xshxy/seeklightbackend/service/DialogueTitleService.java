package com.xshxy.seeklightbackend.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "deepseekChatModel")
public interface DialogueTitleService {

    @SystemMessage("""
            你是对话标题生成器。
            请根据用户的提问总结一个简洁、准确的问答标题。
            输出要求：
            1. 只输出标题本身，不要解释、不要加引号、不要加标点；
            2. 标题长度严格控制在10到12个汉字或字符；
            3. 保留用户问题的核心主题，避免使用“关于”“问题”“对话”等空泛词；
            4. 不要输出换行、Markdown 或编号。
            """)
    String summarize(@UserMessage String userQuestion);
}
