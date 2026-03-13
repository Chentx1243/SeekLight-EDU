package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.dto.UserDetailDto;
import com.xshxy.seeklightbackend.domain.dto.UserListItemDto;
import com.xshxy.seeklightbackend.domain.request.CreateUserRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateUserRequest;
import com.xshxy.seeklightbackend.service.TUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private TUserService userService;

    @GetMapping
    @Operation(summary = "查询用户列表", description = "分页查询系统用户信息")
    public Result<Page<UserListItemDto>> listUsers(
            @Parameter(description = "当前页码")
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页大小")
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return Result.success(userService.pageUsers(current, size));
    }

    @PostMapping
    @Operation(summary = "新增用户", description = "管理员创建系统用户")
    public Result<UserDetailDto> createUser(@RequestBody CreateUserRequest request) {
        return Result.success(userService.createUser(request));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "查询用户详情", description = "根据用户 ID 查询用户详情")
    public Result<UserDetailDto> getUser(@PathVariable Integer userId) {
        return Result.success(userService.getUserDetail(userId));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "修改用户", description = "根据用户 ID 更新用户信息")
    public Result<UserDetailDto> updateUser(@PathVariable Integer userId, @RequestBody UpdateUserRequest request) {
        return Result.success(userService.updateUser(userId, request));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "删除用户", description = "根据用户 ID 删除用户")
    public Result<String> deleteUser(@PathVariable Integer userId) {
        boolean removed = userService.deleteUser(userId);
        if (!removed) {
            return Result.failure("删除失败");
        }
        return Result.success("删除成功");
    }
}
