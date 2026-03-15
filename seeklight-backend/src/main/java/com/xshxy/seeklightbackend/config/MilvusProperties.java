package com.xshxy.seeklightbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "langchain4j.milvus")
public class MilvusProperties {

    private String host;

    private Integer port;

    private String username;

    private String password;

    private String collectionName = "seeklight_knowledge_base";

    /**
     * Must match the dimension of the embedding model used by the project.
     */
    private Integer dimension = 1024;

    private String indexType = "FLAT";

    private String metricType = "COSINE";

    private String consistencyLevel = "EVENTUALLY";

    private Boolean autoFlushOnInsert = true;

    private String idFieldName = "id";

    private String textFieldName = "text";

    private String metadataFieldName = "metadata";

    private String vectorFieldName = "vector";
}
