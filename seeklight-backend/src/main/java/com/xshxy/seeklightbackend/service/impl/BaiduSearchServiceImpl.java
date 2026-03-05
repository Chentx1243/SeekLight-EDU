package com.xshxy.seeklightbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xshxy.seeklightbackend.config.BaiduSearchProperties;
import com.xshxy.seeklightbackend.domain.request.BaiduSearchRequest;
import com.xshxy.seeklightbackend.domain.resp.BaiduSearchResponse;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.BaiduSearchService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;

/**
 * 百度搜索服务实现类
 */
@Slf4j
@Service
public class BaiduSearchServiceImpl implements BaiduSearchService {

    private static final String SEARCH_URL = "https://qianfan.baidubce.com/v2/ai_search/web_search";

    @Resource
    private BaiduSearchProperties baiduSearchProperties;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public BaiduSearchResponse search(BaiduSearchRequest request) {
        try {
            // 设置默认值
            if (request.getSearchSource() == null || request.getSearchSource().isEmpty()) {
                request.setSearchSource("baidu_search_v2");
            }

            if (request.getEdition() == null || request.getEdition().isEmpty()) {
                request.setEdition("standard");
            }

            if (request.getResourceTypeFilter() == null || request.getResourceTypeFilter().isEmpty()) {
                request.setResourceTypeFilter(Arrays.asList(
                        BaiduSearchRequest.SearchResource.builder()
                                .type("web")
                                .topK(20)
                                .build()
                ));
            }

            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(baiduSearchProperties.getApiKey());

            // 构建请求体
            String requestBody = objectMapper.writeValueAsString(request);

            log.info("百度搜索请求: URL={}, Body={}", SEARCH_URL, requestBody);

            // 发送请求
            HttpEntity<String> httpEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    SEARCH_URL,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            String responseBody = responseEntity.getBody();
            log.info("百度搜索响应: Status={}, Body={}", responseEntity.getStatusCode(), responseBody);

            // 解析响应
            BaiduSearchResponse response = objectMapper.readValue(responseBody, BaiduSearchResponse.class);

            // 检查是否有错误
            if (response.getCode() != null && !response.getCode().isEmpty()) {
                throw new BusinessException("百度搜索失败: " + response.getMessage());
            }

            return response;

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("百度搜索HTTP错误: Status={}, Body={}", e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException("百度搜索请求失败: " + e.getStatusCode() + " - " + e.getMessage());
        } catch (Exception e) {
            log.error("百度搜索异常", e);
            throw new BusinessException("百度搜索异常: " + e.getMessage());
        }
    }

    @Override
    public BaiduSearchResponse search(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new BusinessException("搜索内容不能为空");
        }

        BaiduSearchRequest request = BaiduSearchRequest.builder()
                .messages(Collections.singletonList(
                        BaiduSearchRequest.Message.builder()
                                .role("user")
                                .content(query)
                                .build()
                )
                )
                .build();

        return search(request);
    }
}
