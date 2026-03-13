package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.constant.SecurityRoles;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.request.LoginRequest;
import com.xshxy.seeklightbackend.domain.request.RegisterRequest;
import com.xshxy.seeklightbackend.mapper.TUserMapper;
import com.xshxy.seeklightbackend.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Tag(name = "认证接口")
@RestController
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TUserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验用户名和密码，返回 JWT")
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

            String token = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getAuthorities().iterator().next().getAuthority()
            );

            return Result.success(token);
        } catch (AuthenticationException e) {
            return Result.failure("用户名或密码错误");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "创建新用户并返回注册结果")
    public Result<String> register(@RequestBody RegisterRequest request) {
        TUser existing = userMapper.selectByAccount(request.getUsername());
        if (existing != null) {
            return Result.failure("用户名已存在");
        }

        TUser user = new TUser();
        user.setUserAccount(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(SecurityRoles.USER);
        user.setEnabled(1);
        user.setGroupId(1);
        user.setIsDeleted(0);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());

        userMapper.insert(user);

        return Result.success("注册成功");
    }
}
