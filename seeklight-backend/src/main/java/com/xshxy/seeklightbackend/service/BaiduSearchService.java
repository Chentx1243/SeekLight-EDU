package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.request.BaiduSearchRequest;
import com.xshxy.seeklightbackend.domain.resp.BaiduSearchResponse;

/**
 * 百度搜索服务接口
 */
public interface BaiduSearchService {

    /**
     * 执行百度搜索
     *
     * @param request 搜索请求对象
     * @return 搜索响应结果
     */
    BaiduSearchResponse search(BaiduSearchRequest request);

    /**
     * 执行百度搜索（简化版，仅传入查询内容）
     *
     * @param query 搜索查询内容
     * @return 搜索响应结果
     */
    BaiduSearchResponse search(String query);
}
