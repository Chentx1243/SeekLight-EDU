package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TGroupProviderCredential;
import com.xshxy.seeklightbackend.domain.TModelProvider;
import com.xshxy.seeklightbackend.domain.dto.GroupProviderCredentialListItemDto;
import com.xshxy.seeklightbackend.domain.request.CreateGroupProviderCredentialRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateGroupProviderCredentialRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TGroupProviderCredentialMapper;
import com.xshxy.seeklightbackend.service.TGroupProviderCredentialService;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TModelProviderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @author 陈凯宁
* @description 针对表【t_group_provider_credential(分组-供应商凭据映射表)】的数据库操作Service实现
* @createDate 2026-03-02 11:51:49
*/
@Service
public class TGroupProviderCredentialServiceImpl extends ServiceImpl<TGroupProviderCredentialMapper, TGroupProviderCredential>
    implements TGroupProviderCredentialService{

    @Resource
    private TGroupService groupService;

    @Resource
    private TModelProviderService providerService;

    @Override
    public Page<GroupProviderCredentialListItemDto> pageCredentials(Integer current, Integer size, Integer groupId, Integer providerId) {
        Page<GroupProviderCredentialListItemDto> page = new Page<>(current, size);
        return baseMapper.selectCredentialPage(page, groupId, providerId);
    }

    @Override
    public TGroupProviderCredential getCredential(Integer id) {
        if (id == null) {
            throw new BusinessException("凭据ID不能为空");
        }
        TGroupProviderCredential credential = getById(id);
        if (credential == null) {
            throw new BusinessException("凭据配置不存在");
        }
        return credential;
    }

    @Override
    public TGroupProviderCredential createCredential(CreateGroupProviderCredentialRequest request) {
        if (request == null) {
            throw new BusinessException("凭据信息不能为空");
        }
        validateRequiredFields(request.getGroupId(), request.getProviderId(), request.getApiKey());
        validateGroupAndProvider(request.getGroupId(), request.getProviderId());
        ensureUnique(request.getGroupId(), request.getProviderId(), null);

        Date now = new Date();
        TGroupProviderCredential credential = new TGroupProviderCredential();
        credential.setGroupId(request.getGroupId());
        credential.setProviderId(request.getProviderId());
        credential.setApiKey(request.getApiKey().trim());
        credential.setIsDeleted(0);
        credential.setCreatedAt(now);
        credential.setUpdatedAt(now);
        save(credential);
        return credential;
    }

    @Override
    public TGroupProviderCredential updateCredential(Integer id, UpdateGroupProviderCredentialRequest request) {
        if (id == null) {
            throw new BusinessException("凭据ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("凭据信息不能为空");
        }

        TGroupProviderCredential credential = getById(id);
        if (credential == null) {
            throw new BusinessException("凭据配置不存在");
        }

        Integer groupId = request.getGroupId() != null ? request.getGroupId() : credential.getGroupId();
        Integer providerId = request.getProviderId() != null ? request.getProviderId() : credential.getProviderId();

        if (request.getGroupId() != null || request.getProviderId() != null) {
            validateGroupAndProvider(groupId, providerId);
            ensureUnique(groupId, providerId, id);
            credential.setGroupId(groupId);
            credential.setProviderId(providerId);
        }

        if (request.getApiKey() != null) {
            if (!StringUtils.hasText(request.getApiKey())) {
                throw new BusinessException("API Key不能为空");
            }
            credential.setApiKey(request.getApiKey().trim());
        }

        credential.setUpdatedAt(new Date());
        updateById(credential);
        return credential;
    }

    @Override
    public boolean deleteCredential(Integer id) {
        if (id == null) {
            throw new BusinessException("凭据ID不能为空");
        }
        TGroupProviderCredential credential = getById(id);
        if (credential == null) {
            throw new BusinessException("凭据配置不存在");
        }
        return removeById(id);
    }

    private void validateRequiredFields(Integer groupId, Integer providerId, String apiKey) {
        if (groupId == null) {
            throw new BusinessException("分组ID不能为空");
        }
        if (providerId == null) {
            throw new BusinessException("供应商ID不能为空");
        }
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException("API Key不能为空");
        }
    }

    private void validateGroupAndProvider(Integer groupId, Integer providerId) {
        TGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        TModelProvider provider = providerService.getById(providerId);
        if (provider == null) {
            throw new BusinessException("供应商不存在");
        }
    }

    private void ensureUnique(Integer groupId, Integer providerId, Integer excludeId) {
        LambdaQueryWrapper<TGroupProviderCredential> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TGroupProviderCredential::getGroupId, groupId)
                .eq(TGroupProviderCredential::getProviderId, providerId);
        if (excludeId != null) {
            wrapper.ne(TGroupProviderCredential::getId, excludeId);
        }
        if (getOne(wrapper) != null) {
            throw new BusinessException("该分组与供应商的凭据配置已存在");
        }
    }
}




