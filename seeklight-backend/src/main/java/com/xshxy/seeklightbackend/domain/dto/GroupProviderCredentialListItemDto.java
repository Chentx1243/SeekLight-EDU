package com.xshxy.seeklightbackend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "GroupProviderCredentialListItemDto", description = "分组供应商凭据列表项")
@Data
public class GroupProviderCredentialListItemDto {

    @Schema(description = "主键 ID")
    private Integer id;

    @Schema(description = "分组 ID")
    private Integer groupId;

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "供应商 ID")
    private Integer providerId;

    @Schema(description = "供应商名称")
    private String providerName;

    @Schema(description = "API Key")
    private String apiKey;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
