package com.xshxy.seeklightbackend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.domain.TGroupProviderCredential;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xshxy.seeklightbackend.domain.dto.GroupProviderCredentialListItemDto;
import org.apache.ibatis.annotations.Param;

/**
* @author 陈凯宁
* @description 针对表【t_group_provider_credential(分组-供应商凭据映射表)】的数据库操作Mapper
* @createDate 2026-03-02 11:51:49
* @Entity com.xshxy.seeklightbackend.domain.TGroupProviderCredential
*/
public interface TGroupProviderCredentialMapper extends BaseMapper<TGroupProviderCredential> {

    Page<GroupProviderCredentialListItemDto> selectCredentialPage(Page<GroupProviderCredentialListItemDto> page,
                                                                  @Param("groupId") Integer groupId,
                                                                  @Param("providerId") Integer providerId);
}




