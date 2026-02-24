package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TGroupModelPermission;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TGroupModelPermissionService;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TModelService;
import com.xshxy.seeklightbackend.mapper.TGroupModelPermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
* @author 陈凯宁
* @description 针对表【t_group_model_permission(分组模型权限表)】的数据库操作Service实现
* @createDate 2026-02-24 17:06:46
*/
@Service
public class TGroupModelPermissionServiceImpl extends ServiceImpl<TGroupModelPermissionMapper, TGroupModelPermission>
    implements TGroupModelPermissionService{

    @Resource
    private TGroupService groupService;

    @Resource
    private TModelService modelService;

    @Override
    public TGroupModelPermission addPermission(TGroupModelPermission permission) {
        if (permission == null) {
            throw new BusinessException("权限信息不能为空");
        }
        if (permission.getGroupId() == null) {
            throw new BusinessException("分组ID不能为空");
        }
        if (permission.getModelId() == null) {
            throw new BusinessException("模型ID不能为空");
        }

        // 校验分组是否存在并获取分组名称
        TGroup group = groupService.getById(permission.getGroupId());
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        permission.setGroupName(group.getGroupName());

        // 校验模型是否存在并获取模型名称
        com.xshxy.seeklightbackend.domain.TModel model = modelService.getById(permission.getModelId());
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        permission.setModelName(model.getModelName());

        // 检查是否已存在相同的权限记录
        LambdaQueryWrapper<TGroupModelPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TGroupModelPermission::getGroupId, permission.getGroupId())
                .eq(TGroupModelPermission::getModelId, permission.getModelId());
        TGroupModelPermission existing = this.getOne(wrapper);
        if (existing != null) {
            throw new BusinessException("该分组已存在此模型的权限配置");
        }

        // 设置默认值
        if (permission.getVisible() == null) {
            permission.setVisible(1);
        }
        if (permission.getCallable() == null) {
            permission.setCallable(1);
        }

        this.save(permission);
        return permission;
    }

    @Override
    public Page<TGroupModelPermission> queryPermissions(Integer current, Integer size,
                                                         Integer groupId, String groupName,
                                                         Integer modelId, String modelName,
                                                         Integer visible, Integer callable) {
        Page<TGroupModelPermission> page = new Page<>(current, size);
        LambdaQueryWrapper<TGroupModelPermission> wrapper = new LambdaQueryWrapper<>();

        if (groupId != null) {
            wrapper.eq(TGroupModelPermission::getGroupId, groupId);
        }
        if (groupName != null && !groupName.trim().isEmpty()) {
            wrapper.like(TGroupModelPermission::getGroupName, groupName.trim());
        }
        if (modelId != null) {
            wrapper.eq(TGroupModelPermission::getModelId, modelId);
        }
        if (modelName != null && !modelName.trim().isEmpty()) {
            wrapper.like(TGroupModelPermission::getModelName, modelName.trim());
        }
        if (visible != null) {
            wrapper.eq(TGroupModelPermission::getVisible, visible);
        }
        if (callable != null) {
            wrapper.eq(TGroupModelPermission::getCallable, callable);
        }

        wrapper.orderByDesc(TGroupModelPermission::getId);
        return this.page(page, wrapper);
    }

    @Override
    public boolean deletePermission(Integer id) {
        if (id == null) {
            throw new BusinessException("权限ID不能为空");
        }
        TGroupModelPermission permission = this.getById(id);
        if (permission == null) {
            throw new BusinessException("权限记录不存在");
        }
        return this.removeById(id);
    }

    @Override
    public TGroupModelPermission updatePermission(Integer id, TGroupModelPermission permission) {
        if (id == null) {
            throw new BusinessException("权限ID不能为空");
        }
        if (permission == null) {
            throw new BusinessException("权限信息不能为空");
        }

        TGroupModelPermission existing = this.getById(id);
        if (existing == null) {
            throw new BusinessException("权限记录不存在");
        }

        // 如果修改了分组ID，需要校验是否存在并同步更新分组名称
        if (permission.getGroupId() != null && !permission.getGroupId().equals(existing.getGroupId())) {
            TGroup group = groupService.getById(permission.getGroupId());
            if (group == null) {
                throw new BusinessException("分组不存在");
            }
            existing.setGroupId(permission.getGroupId());
            existing.setGroupName(group.getGroupName());
        }

        // 如果修改了模型ID，需要校验是否存在并同步更新模型名称
        if (permission.getModelId() != null && !permission.getModelId().equals(existing.getModelId())) {
            com.xshxy.seeklightbackend.domain.TModel model = modelService.getById(permission.getModelId());
            if (model == null) {
                throw new BusinessException("模型不存在");
            }
            existing.setModelId(permission.getModelId());
            existing.setModelName(model.getModelName());
        }

        // 检查修改后的组合是否已存在
        LambdaQueryWrapper<TGroupModelPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TGroupModelPermission::getGroupId, existing.getGroupId())
                .eq(TGroupModelPermission::getModelId, existing.getModelId())
                .ne(TGroupModelPermission::getId, id);
        if (this.getOne(wrapper) != null) {
            throw new BusinessException("已存在相同分组和模型的权限配置");
        }

        if (permission.getVisible() != null) {
            existing.setVisible(permission.getVisible());
        }
        if (permission.getCallable() != null) {
            existing.setCallable(permission.getCallable());
        }

        this.updateById(existing);
        return existing;
    }
}




