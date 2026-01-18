package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TGroupService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Resource
    private TGroupService groupService;

    @PostMapping
    public Result<TGroup> createGroup(@RequestBody TGroup request) {
        if (request == null) {
            throw new BusinessException("分组信息不能为空");
        }
        if (!StringUtils.hasText(request.getGroupName())) {
            throw new BusinessException("分组名称不能为空");
        }
        if (!StringUtils.hasText(request.getGroupApiKey())) {
            throw new BusinessException("分组默认key不能为空");
        }
        Date now = new Date();
        TGroup group = new TGroup();
        group.setGroupName(request.getGroupName());
        group.setGroupApiKey(request.getGroupApiKey());
        group.setDescription(request.getDescription());
        group.setIsDeleted(0);
        group.setCreatedAt(now);
        group.setUpdatedAt(now);
        groupService.save(group);
        return Result.success(group);
    }

    @GetMapping
    public Result<List<TGroup>> listGroups(@RequestParam(value = "groupName", required = false) String groupName) {
        LambdaQueryWrapper<TGroup> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(groupName)) {
            queryWrapper.like(TGroup::getGroupName, groupName);
        }
        queryWrapper.orderByDesc(TGroup::getCreatedAt);
        return Result.success(groupService.list(queryWrapper));
    }

    @PutMapping("/{groupId}")
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
            group.setGroupName(request.getGroupName());
        }
        if (request.getGroupApiKey() != null) {
            if (!StringUtils.hasText(request.getGroupApiKey())) {
                throw new BusinessException("分组默认key不能为空");
            }
            group.setGroupApiKey(request.getGroupApiKey());
        }
        if (request.getDescription() != null) {
            group.setDescription(request.getDescription());
        }
        group.setUpdatedAt(new Date());
        groupService.updateById(group);
        return Result.success(group);
    }

    @DeleteMapping("/{groupId}")
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
