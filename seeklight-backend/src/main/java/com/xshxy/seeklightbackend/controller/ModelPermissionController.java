package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroupModelPermission;
import com.xshxy.seeklightbackend.service.TGroupModelPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
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

@Tag(name = "分组模型权限管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/model-permission")
public class ModelPermissionController {

    @Resource
    private TGroupModelPermissionService permissionService;

    @PostMapping
    @Operation(summary = "添加权限", description = "为某个用户组添加某个模型的权限，groupName 和 modelName 会根据 groupId 和 modelId 自动填充")
    public Result<TGroupModelPermission> addPermission(@RequestBody TGroupModelPermission permission) {
        TGroupModelPermission result = permissionService.addPermission(permission);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "查询权限列表", description = "按照可选条件查询符合的权限记录，支持分页")
    @Parameters({
            @io.swagger.v3.oas.annotations.Parameter(description = "当前页", name = "current", required = true),
            @io.swagger.v3.oas.annotations.Parameter(description = "每页大小", name = "size", required = true),
            @io.swagger.v3.oas.annotations.Parameter(description = "分组 ID", name = "groupId"),
            @io.swagger.v3.oas.annotations.Parameter(description = "分组名称", name = "groupName"),
            @io.swagger.v3.oas.annotations.Parameter(description = "模型 ID", name = "modelId"),
            @io.swagger.v3.oas.annotations.Parameter(description = "模型名称", name = "modelName"),
            @io.swagger.v3.oas.annotations.Parameter(description = "是否可见，0 不可见，1 可见", name = "visible"),
            @io.swagger.v3.oas.annotations.Parameter(description = "是否可调用，0 不可调用，1 可调用", name = "callable")
    })
    public Result<Page<TGroupModelPermission>> queryPermissions(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "groupName", required = false) String groupName,
            @RequestParam(value = "modelId", required = false) Integer modelId,
            @RequestParam(value = "modelName", required = false) String modelName,
            @RequestParam(value = "visible", required = false) Integer visible,
            @RequestParam(value = "callable", required = false) Integer callable) {
        Page<TGroupModelPermission> page = permissionService.queryPermissions(
                current, size, groupId, groupName, modelId, modelName, visible, callable
        );
        return Result.success(page);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "删除某条可用权限记录")
    public Result<String> deletePermission(@PathVariable Integer id) {
        boolean deleted = permissionService.deletePermission(id);
        if (!deleted) {
            return Result.failure("删除失败");
        }
        return Result.success("删除成功");
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改权限", description = "修改权限信息")
    public Result<TGroupModelPermission> updatePermission(
            @PathVariable Integer id,
            @RequestBody TGroupModelPermission permission) {
        TGroupModelPermission result = permissionService.updatePermission(id, permission);
        return Result.success(result);
    }
}
