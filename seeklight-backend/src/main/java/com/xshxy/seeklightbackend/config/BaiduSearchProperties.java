package com.xshxy.seeklightbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * 百度搜索配置属性类
 */
@Component
@ConfigurationProperties(prefix = "baidu.search")
@Data
public class BaiduSearchProperties {

    /**
     * 百度千帆平台 API Key
     */
    private String apiKey;

    /**
     * 搜索版本，默认standard
     * 可选值：
     * standard：完整版本
     * lite：标准版本，时延表现更好
     */
    private String edition = "standard";

    /**
     * 默认搜索结果数量，默认20
     */
    private Integer defaultTopK = 5;

    /**
     * 是否启用安全搜索，默认false
     */
    private Boolean safeSearch = false;

    /**
     * 连接超时时间（毫秒），默认30000
     */
    private Integer connectTimeout = 30000;

    /**
     * 读取超时时间（毫秒），默认60000
     */
    private Integer readTimeout = 60000;
}
