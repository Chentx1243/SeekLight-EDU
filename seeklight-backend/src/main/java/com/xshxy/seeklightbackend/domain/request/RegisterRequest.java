package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "RegisterRequest", description = "注册请求")
@Data
public class RegisterRequest {
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;
    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @Schema(description = "姓名/昵称")
    private String name;
    @Schema(description = "角色，默认 USER")
    private String role;
}
