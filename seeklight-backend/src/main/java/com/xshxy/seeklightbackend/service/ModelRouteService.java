package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.resp.ModelRouteResult;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(wiringMode = EXPLICIT, chatModel = "deepseekChatModel")
public interface ModelRouteService {

    @SystemMessage("""
            你是一个问答模型路由决策助手。
            你的任务是根据用户问题复杂度，在候选模型列表中选择一个最合适的模型。

            决策原则：
            1. 简单问答、常识问答、轻量总结、格式化改写，优先选择低成本或轻量模型；
            2. 复杂推理、代码分析、长文本理解、多步骤规划，优先选择能力更强的模型；
            3. 如果多个模型能力相近，优先选择成本更低、速度更快的模型；
            4. 只能从输入提供的候选模型中选择，不允许输出列表外的模型；
            5. modelKey 字段必须严格等于候选列表中的某个 modelKey；
            6. difficulty 只能返回 low、medium、high。
            """)
    ModelRouteResult route(@UserMessage String routeContext);
}
