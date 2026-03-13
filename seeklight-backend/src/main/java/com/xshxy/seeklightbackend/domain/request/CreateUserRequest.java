package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "CreateUserRequest", description = "新增用户请求")
@Data
public class CreateUserRequest {

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "登录密码")
    private String password;

    @Schema(description = "用户名称")
    private String name;

    @Schema(description = "所属分组 ID")
    private Integer groupId;

    @Schema(description = "系统角色，ROLE_ADMIN 或 ROLE_USER")
    private String role;

    @Schema(description = "账户启用状态，1 启用，0 禁用")
    private Integer enabled;
}
