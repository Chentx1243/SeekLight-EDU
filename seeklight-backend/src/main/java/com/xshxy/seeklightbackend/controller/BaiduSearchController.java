package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.request.BaiduSearchRequest;
import com.xshxy.seeklightbackend.domain.resp.BaiduSearchResponse;
import com.xshxy.seeklightbackend.service.BaiduSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 百度搜索接口控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/search")
@Tag(name = "百度搜索接口", description = "提供百度联网搜索功能")
public class BaiduSearchController {

    @Resource
    private BaiduSearchService baiduSearchService;

    /**
     * 执行百度搜索（完整请求）
     *
     * @param request 搜索请求对象
     * @return 搜索响应结果
     */
    @PostMapping("/baidu")
    @Operation(summary = "百度搜索", description = "根据请求参数执行百度搜索，返回搜索结果")
    public Result<BaiduSearchResponse> search(
            @Parameter(description = "搜索请求对象")
            @RequestBody BaiduSearchRequest request) {
        try {
            log.info("接收到百度搜索请求: {}", request);
            BaiduSearchResponse response = baiduSearchService.search(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("百度搜索失败", e);
            return Result.failure("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 执行百度搜索（简化版，仅传入查询内容）
     *
     * @param query 搜索查询内容
     * @return 搜索响应结果
     */
    @GetMapping("/baidu")
    @Operation(summary = "百度搜索（简化版）", description = "根据查询内容执行百度搜索，返回搜索结果")
    public Result<BaiduSearchResponse> searchByQuery(
            @Parameter(description = "搜索查询内容")
            @RequestParam("query") String query) {
        try {
            log.info("接收到百度搜索请求: query={}", query);
            BaiduSearchResponse response = baiduSearchService.search(query);
            return Result.success(response);
        } catch (Exception e) {
            log.error("百度搜索失败", e);
            return Result.failure("搜索失败: " + e.getMessage());
        }
    }

    /**
     * 执行百度搜索（指定返回结果数量）
     *
     * @param query 搜索查询内容
     * @param topK  返回结果数量
     * @return 搜索响应结果
     */
    @GetMapping("/baidu/{topK}")
    @Operation(summary = "百度搜索（指定结果数量）", description = "根据查询内容和指定数量执行百度搜索")
    public Result<BaiduSearchResponse> searchWithTopK(
            @Parameter(description = "搜索查询内容")
            @RequestParam("query") String query,
            @Parameter(description = "返回结果数量")
            @PathVariable("topK") Integer topK) {
        try {
            log.info("接收到百度搜索请求: query={}, topK={}", query, topK);

            BaiduSearchRequest request = BaiduSearchRequest.builder()
                    .messages(java.util.Collections.singletonList(
                            BaiduSearchRequest.Message.builder()
                                    .role("user")
                                    .content(query)
                                    .build()
                    ))
                    .resourceTypeFilter(java.util.Collections.singletonList(
                            BaiduSearchRequest.SearchResource.builder()
                                    .type("web")
                                    .topK(topK)
                                    .build()
                    ))
                    .build();

            BaiduSearchResponse response = baiduSearchService.search(request);
            return Result.success(response);
        } catch (Exception e) {
            log.error("百度搜索失败", e);
            return Result.failure("搜索失败: " + e.getMessage());
        }
    }
}
