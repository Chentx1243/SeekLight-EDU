package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroupModelPermission;
import com.xshxy.seeklightbackend.service.TGroupModelPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@Tag(name = "分组模型权限管理")
@RestController
@RequestMapping("/model-permission")
public class ModelPermissionController {

    @Resource
    private TGroupModelPermissionService permissionService;

    @PostMapping
    @Operation(summary = "添加权限", description = "为某个用户组添加某个模型的权限")
    public Result<TGroupModelPermission> addPermission(@RequestBody TGroupModelPermission permission) {
        TGroupModelPermission result = permissionService.addPermission(permission);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "查询权限列表", description = "按照可选条件查询符合的列表，支持分页")
    @Parameters({
            @io.swagger.v3.oas.annotations.Parameter(description = "当前页", name = "current", required = true),
            @io.swagger.v3.oas.annotations.Parameter(description = "每页大小", name = "size", required = true),
            @io.swagger.v3.oas.annotations.Parameter(description = "分组ID", name = "groupId"),
            @io.swagger.v3.oas.annotations.Parameter(description = "模型ID", name = "modelId"),
            @io.swagger.v3.oas.annotations.Parameter(description = "是否可见（0-不可见，1-可见）", name = "visible"),
            @io.swagger.v3.oas.annotations.Parameter(description = "是否可调用（0-不可调用，1-可调用）", name = "callable")
    })
    public Result<Page<TGroupModelPermission>> queryPermissions(
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @RequestParam(value = "modelId", required = false) Integer modelId,
            @RequestParam(value = "visible", required = false) Integer visible,
            @RequestParam(value = "callable", required = false) Integer callable) {
        Page<TGroupModelPermission> page = permissionService.queryPermissions(current, size, groupId, modelId, visible, callable);
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
    public Result<TGroupModelPermission> updatePermission(@PathVariable Integer id,
                                                          @RequestBody TGroupModelPermission permission) {
        TGroupModelPermission result = permissionService.updatePermission(id, permission);
        return Result.success(result);
    }
}
