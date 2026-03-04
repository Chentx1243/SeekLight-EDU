package com.xshxy.seeklightbackend.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于配置基础的模型组件配置类
 */
@Configuration
public class BaseModelConfig {

    private final AiModelsProperties aiModelsProperties;

    public BaseModelConfig(AiModelsProperties aiModelsProperties) {
        this.aiModelsProperties = aiModelsProperties;
    }

    /**
     * DeepSeek-chat模型 对话基础模型bean
     * @return
     */
    @Bean("deepseekChatModel")
    public ChatModel deepseekChatModel() {
        AiModelsProperties.ModelConfig deepseek = aiModelsProperties.getConfigs().get("deepseek");
        return OpenAiChatModel.builder()
                .apiKey(deepseek.getApiKey())
                .baseUrl(deepseek.getBaseUrl())
                .modelName(deepseek.getModelName())
                .temperature(deepseek.getTemperature())
                .build();
    }
}
