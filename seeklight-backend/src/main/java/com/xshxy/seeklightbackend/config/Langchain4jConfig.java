package com.xshxy.seeklightbackend.config;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenCountEstimator;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Langchain4jConfig {

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    /**
     * RAG：文本切断bean：用于将document按照特定规则切断chunk
     * @return
     */
//    @Bean
//    public DocumentSplitter documentSplitter() {
//        return DocumentSplitters.recursive(
//                800,   // 每个 chunk 最大 token 数
//                100,   // overlap token 数
//                // 这里是指利用gpt-4o-mini模型的规则来判断token长度（并非调用模型）
//                new OpenAiTokenCountEstimator("gpt-4o-mini")
//        );
//    }

    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor(){
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(
                        DocumentSplitters.recursive(
                                1000,
                                200,
                                new OpenAiTokenCountEstimator("gpt-4o-mini")
                        )
                )
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }


}
