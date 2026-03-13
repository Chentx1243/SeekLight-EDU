package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "AddModelPermissionRequest", description = "新增分组模型权限请求")
@Data
public class AddModelPermissionRequest {

    @Schema(description = "用户分组 ID")
    private Integer groupId;

    @Schema(description = "模型 ID")
    private Integer modelId;

    @Schema(description = "是否可见，0 不可见，1 可见")
    private Integer visible;

    @Schema(description = "是否可调用，0 不可调用，1 可调用")
    private Integer callable;
}
