package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "CreateModelRequest", description = "新增模型请求")
@Data
public class CreateModelRequest {

    @Schema(description = "模型展示名称")
    private String modelName;

    @Schema(description = "模型简介")
    private String description;

    @Schema(description = "模型提供方 ID")
    private Integer provider;

    @Schema(description = "模型请求标识")
    private String modelKey;

    @Schema(description = "模型状态，1 上架，0 下架")
    private Integer status;
}
