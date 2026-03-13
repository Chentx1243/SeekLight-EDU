package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "UpdateGroupProviderCredentialRequest", description = "修改分组供应商凭据请求")
@Data
public class UpdateGroupProviderCredentialRequest {

    @Schema(description = "分组 ID")
    private Integer groupId;

    @Schema(description = "供应商 ID")
    private Integer providerId;

    @Schema(description = "该分组在该供应商下使用的 API Key")
    private String apiKey;
}
