package com.xshxy.seeklightbackend.request;

import lombok.Data;

@Data
public class RegisterRequest {
    // 用户账号
    private String username;
    // 用户密码（明文）
    private String password;
    // 用户姓名（昵称）
    private String name;
    // 用户角色
    private String role; // 可选：默认USER
}
