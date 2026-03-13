package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@Tag(name = "分组管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/group")
public class GroupController {

    @Resource
    private TGroupService groupService;

    @PostMapping
    @Operation(summary = "新增分组", description = "创建新的分组配置")
    public Result<TGroup> createGroup(@RequestBody TGroup request) {
        if (request == null) {
            throw new BusinessException("分组信息不能为空");
        }
        if (!StringUtils.hasText(request.getGroupName())) {
            throw new BusinessException("分组名称不能为空");
        }
        if (!StringUtils.hasText(request.getGroupApiKey())) {
            throw new BusinessException("分组默认 key 不能为空");
        }

        Date now = new Date();
        TGroup group = new TGroup();
        group.setGroupName(request.getGroupName().trim());
        group.setGroupApiKey(request.getGroupApiKey().trim());
        group.setDescription(request.getDescription());
        group.setIsDeleted(0);
        group.setCreatedAt(now);
        group.setUpdatedAt(now);
        groupService.save(group);
        return Result.success(group);
    }

    @GetMapping
    @Operation(summary = "查询分组列表", description = "支持按分组名称模糊匹配")
    public Result<List<TGroup>> listGroups(
            @Parameter(description = "分组名称，模糊匹配")
            @RequestParam(value = "groupName", required = false) String groupName) {
        LambdaQueryWrapper<TGroup> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(groupName)) {
            queryWrapper.like(TGroup::getGroupName, groupName.trim());
        }
        queryWrapper.orderByDesc(TGroup::getCreatedAt);
        return Result.success(groupService.list(queryWrapper));
    }

    @GetMapping("/{groupId}")
    @Operation(summary = "查询分组详情", description = "根据分组 ID 查询分组详情")
    public Result<TGroup> getGroup(@PathVariable Integer groupId) {
        if (groupId == null) {
            throw new BusinessException("分组ID不能为空");
        }
        TGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        return Result.success(group);
    }

    @PutMapping("/{groupId}")
    @Operation(summary = "修改分组", description = "按分组 ID 更新分组信息")
    public Result<TGroup> updateGroup(@PathVariable Integer groupId, @RequestBody TGroup request) {
        if (groupId == null) {
            throw new BusinessException("分组ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("分组信息不能为空");
        }

        TGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        if (request.getGroupName() != null) {
            if (!StringUtils.hasText(request.getGroupName())) {
                throw new BusinessException("分组名称不能为空");
            }
            group.setGroupName(request.getGroupName().trim());
        }
        if (request.getGroupApiKey() != null) {
            if (!StringUtils.hasText(request.getGroupApiKey())) {
                throw new BusinessException("分组默认 key 不能为空");
            }
            group.setGroupApiKey(request.getGroupApiKey().trim());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        group.setUpdatedAt(new Date());
        groupService.updateById(group);
        return Result.success(group);
    }

    @DeleteMapping("/{groupId}")
    @Operation(summary = "删除分组", description = "按分组 ID 删除分组")
    public Result<String> deleteGroup(@PathVariable Integer groupId) {
        if (groupId == null) {
            throw new BusinessException("分组ID不能为空");
        }
        boolean removed = groupService.removeById(groupId);
        if (!removed) {
            return Result.failure("分组不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
