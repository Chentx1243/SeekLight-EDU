package com.xshxy.seeklightbackend.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置基本Ai模型列表
 */
@Configuration
public class BaseModelConfig {

    private final AiModelsProperties aiModelsProperties;

    public BaseModelConfig(AiModelsProperties aiModelsProperties) {
        this.aiModelsProperties = aiModelsProperties;
    }

    /**
     * deepseek基础模型；用于实现项目中的意图识别等操作
     * @return 模型基础实例
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

    /**
     * 向量模型定义，用于实现项目中的文本向量化等操作
     * @return 向量模型
     */
    @Bean("textEmbeddingV4Model")
    public EmbeddingModel textEmbeddingV4Model() {
        AiModelsProperties.ModelConfig embedding = aiModelsProperties.getConfigs().get("text-embedding-v4");
        return OpenAiEmbeddingModel.builder()
                .apiKey(embedding.getApiKey())
                .baseUrl(embedding.getBaseUrl())
                .modelName(embedding.getModelName())
                // 注意：这里的维度，需要与向量库的维度保持一致: 1024
                .dimensions(embedding.getDimensions())
                .build();
    }
}
