package com.xshxy.seeklightbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "agent.fastgpt")
public class FastGptProperties {

    private String baseUrl = "";

    private long connectTimeoutMs = 30000L;

    private long readTimeoutMs = 600000L;

    private int maxMessages = 10;
}
