package com.xshxy.seeklightbackend.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import io.milvus.common.clientenum.ConsistencyLevelEnum;
import io.milvus.param.IndexType;
import io.milvus.param.MetricType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class MilvusVectorStoreConfig {

    @Bean
    public EmbeddingStore<TextSegment> milvusEmbeddingStore(MilvusProperties properties) {
        MilvusEmbeddingStore.Builder builder = MilvusEmbeddingStore.builder()
                .host(properties.getHost())
                .port(properties.getPort())
                .collectionName(properties.getCollectionName())
                .dimension(properties.getDimension())
                .indexType(IndexType.valueOf(properties.getIndexType()))
                .metricType(MetricType.valueOf(properties.getMetricType()))
                .consistencyLevel(ConsistencyLevelEnum.valueOf(properties.getConsistencyLevel()))
                .autoFlushOnInsert(Boolean.TRUE.equals(properties.getAutoFlushOnInsert()))
                .idFieldName(properties.getIdFieldName())
                .textFieldName(properties.getTextFieldName())
                .metadataFieldName(properties.getMetadataFieldName())
                .vectorFieldName(properties.getVectorFieldName());

        if (StringUtils.hasText(properties.getUsername())) {
            builder.username(properties.getUsername());
        }
        if (StringUtils.hasText(properties.getPassword())) {
            builder.password(properties.getPassword());
        }

        return builder.build();
    }
}
