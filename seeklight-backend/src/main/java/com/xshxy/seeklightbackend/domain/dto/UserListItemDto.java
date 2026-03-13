package com.xshxy.seeklightbackend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "UserListItemDto", description = "用户管理列表项")
@Data
public class UserListItemDto {

    @Schema(description = "用户 ID")
    private Integer userId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "用户名称")
    private String name;

    @Schema(description = "所属分组名称")
    private String groupName;

    @Schema(description = "系统角色")
    private String role;

    @Schema(description = "账户启用状态，1 启用，0 禁用")
    private Integer enabled;
}
