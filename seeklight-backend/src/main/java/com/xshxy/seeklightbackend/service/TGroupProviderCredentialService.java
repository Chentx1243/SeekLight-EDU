package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.domain.TGroupProviderCredential;
import com.xshxy.seeklightbackend.domain.request.CreateGroupProviderCredentialRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateGroupProviderCredentialRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 陈凯宁
* @description 针对表【t_group_provider_credential(分组-供应商凭据映射表)】的数据库操作Service
* @createDate 2026-03-02 11:51:49
*/
public interface TGroupProviderCredentialService extends IService<TGroupProviderCredential> {

    Page<TGroupProviderCredential> pageCredentials(Integer current, Integer size, Integer groupId, Integer providerId);

    TGroupProviderCredential getCredential(Integer id);

    TGroupProviderCredential createCredential(CreateGroupProviderCredentialRequest request);

    TGroupProviderCredential updateCredential(Integer id, UpdateGroupProviderCredentialRequest request);

    boolean deleteCredential(Integer id);
}
