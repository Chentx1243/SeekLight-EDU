package com.xshxy.seeklightbackend.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Base AI model beans.
 */
@Configuration
public class BaseModelConfig {

    private final AiModelsProperties aiModelsProperties;

    public BaseModelConfig(AiModelsProperties aiModelsProperties) {
        this.aiModelsProperties = aiModelsProperties;
    }

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

    @Bean("textEmbeddingV4Model")
    public EmbeddingModel textEmbeddingV4Model() {
        AiModelsProperties.ModelConfig embedding = aiModelsProperties.getConfigs().get("text-embedding-v4");
        return OpenAiEmbeddingModel.builder()
                .apiKey(embedding.getApiKey())
                .baseUrl(embedding.getBaseUrl())
                .modelName(embedding.getModelName())
                .dimensions(embedding.getDimensions())
                .build();
    }
}
