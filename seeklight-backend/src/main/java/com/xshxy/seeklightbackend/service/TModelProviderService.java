package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.TModelProvider;
import com.xshxy.seeklightbackend.domain.request.ProviderRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_model_provider(模型供应商)】的数据库操作Service
* @createDate 2026-02-24 17:47:48
*/
public interface TModelProviderService extends IService<TModelProvider> {

    /**
     * 创建供应商
     * @param request 供应商请求信息
     * @return 创建的供应商
     */
    TModelProvider createProvider(ProviderRequest request);

    /**
     * 查询供应商列表
     * @param providerName 供应商名称（模糊匹配）
     * @param status 状态（1启用，0禁用）
     * @return 供应商列表
     */
    List<TModelProvider> listProviders(String providerName, Integer status);

    /**
     * 根据ID查询供应商
     * @param id 供应商ID
     * @return 供应商信息
     */
    TModelProvider getProviderById(Integer id);

    /**
     * 更新供应商
     * @param id 供应商ID
     * @param request 更新请求信息
     * @return 更新后的供应商
     */
    TModelProvider updateProvider(Integer id, ProviderRequest request);

    /**
     * 删除供应商
     * @param id 供应商ID
     * @return 是否删除成功
     */
    boolean deleteProvider(Integer id);
}
