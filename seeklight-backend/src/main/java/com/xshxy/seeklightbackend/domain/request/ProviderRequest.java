package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "ProviderRequest", description = "供应商创建/更新请求")
@Data
public class ProviderRequest {

    @Schema(description = "供应商名称")
    private String providerName;

    @Schema(description = "接口地址")
    private String baseUrl;

    @Schema(description = "供应商预设提示词")
    private String promptTemplate;

    @Schema(description = "是否启用（1=启用，0=禁用）")
    private Integer status;
}
