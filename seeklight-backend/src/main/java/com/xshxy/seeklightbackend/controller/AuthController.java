package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.mapper.TUserMapper;
import com.xshxy.seeklightbackend.request.LoginRequest;
import com.xshxy.seeklightbackend.request.RegisterRequest;
import com.xshxy.seeklightbackend.util.JwtUtil;
import opennlp.tools.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil; // 后续生成 JWT

    @Autowired
    private TUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            org.springframework.security.core.userdetails.User user =
                    (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

            // 使用 role + username 生成 JWT
            String token = jwtUtil.generateToken(user.getUsername(),
                    user.getAuthorities().iterator().next().getAuthority());
            
            return Result.success(token);
        } catch (AuthenticationException e) {
            return Result.failure("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    public Result<String> register(@RequestBody RegisterRequest request) {

        // 1. 检查用户名是否已存在
        TUser existing = userMapper.selectByAccount(request.getUsername());
        if (existing != null) {
            return Result.failure("用户名已存在");
        }

        // 2. 创建用户实体
        TUser user = new TUser();
        user.setUserAccount(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // 加密
        user.setName(request.getName());
        user.setRole(request.getRole() != null ? request.getRole() : "USER"); // 默认角色 USER
        user.setEnabled(1); // 启用账号
        user.setGroupId(1); // 默认所属分组为 1
        user.setIsDeleted(0);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        // 3. 插入数据库
        userMapper.insert(user);

        return Result.success("注册成功");
    }
}
