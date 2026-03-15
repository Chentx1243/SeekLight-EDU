package com.xshxy.seeklightbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "ai.models")
public class AiModelsProperties {

    private Map<String, ModelConfig> configs = new HashMap<>();

    @Data
    public static class ModelConfig {
        private String apiKey;
        private String baseUrl;
        private String modelName;
        private Double temperature;
        private Integer dimensions;
    }
}
