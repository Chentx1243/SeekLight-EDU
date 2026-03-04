package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TModelProvider;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TModelProviderMapper;
import com.xshxy.seeklightbackend.domain.request.ProviderRequest;
import com.xshxy.seeklightbackend.service.TModelProviderService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_model_provider(模型供应商)】的数据库操作Service实现
* @createDate 2026-02-24 17:47:48
*/
@Service
public class TModelProviderServiceImpl extends ServiceImpl<TModelProviderMapper, TModelProvider>
    implements TModelProviderService{


    @Override
    public TModelProvider createProvider(ProviderRequest request) {
        if (request == null) {
            throw new BusinessException("请检查入参");
        }
        if (!StringUtils.hasText(request.getProviderName())) {
            throw new BusinessException("供应商名称不能为空");
        }
        if (!StringUtils.hasText(request.getBaseUrl())) {
            throw new BusinessException("接口地址不能为空");
        }


        Date now = new Date();
        TModelProvider provider = new TModelProvider();
        provider.setProviderName(request.getProviderName());
        provider.setBaseUrl(request.getBaseUrl());
        provider.setPromptTemplate(request.getPromptTemplate());
        provider.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        provider.setIsDeleted(0);
        provider.setCreatedAt(now);
        provider.setUpdatedAt(now);

        this.save(provider);
        return provider;
    }

    @Override
    public List<TModelProvider> listProviders(String providerName, Integer status) {
        LambdaQueryWrapper<TModelProvider> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(providerName)) {
            queryWrapper.like(TModelProvider::getProviderName, providerName);
        }
        if (status != null) {
            queryWrapper.eq(TModelProvider::getStatus, status);
        }
        queryWrapper.orderByDesc(TModelProvider::getCreatedAt);
        return this.list(queryWrapper);
    }

    @Override
    public TModelProvider getProviderById(Integer id) {
        if (id == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        TModelProvider provider = this.getById(id);
        if (provider == null) {
            throw new BusinessException("供应商不存在");
        }
        return provider;
    }

    @Override
    public TModelProvider updateProvider(Integer id, ProviderRequest request) {
        if (id == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("供应商信息不能为空");
        }

        TModelProvider provider = this.getById(id);
        if (provider == null) {
            throw new BusinessException("供应商不存在");
        }


        if (request.getProviderName() != null) {
            if (!StringUtils.hasText(request.getProviderName())) {
                throw new BusinessException("供应商名称不能为空");
            }
            provider.setProviderName(request.getProviderName());
        }

        if (request.getBaseUrl() != null) {
            if (!StringUtils.hasText(request.getBaseUrl())) {
                throw new BusinessException("接口地址不能为空");
            }
            provider.setBaseUrl(request.getBaseUrl());
        }

        if (request.getPromptTemplate() != null) {
            provider.setPromptTemplate(request.getPromptTemplate());
        }

        if (request.getStatus() != null) {
            provider.setStatus(request.getStatus());
        }

        provider.setUpdatedAt(new Date());
        this.updateById(provider);
        return provider;
    }

    @Override
    public boolean deleteProvider(Integer id) {
        if (id == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        return this.removeById(id);
    }
}
