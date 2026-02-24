package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TGroupModelPermission;

/**
* @author 陈凯宁
* @description 针对表【t_group_model_permission(分组模型权限表)】的数据库操作Service
* @createDate 2026-02-24 17:06:47
*/
public interface TGroupModelPermissionService extends IService<TGroupModelPermission> {

    /**
     * 添加分组模型权限
     * @param permission 权限信息
     * @return 添加后的权限信息
     */
    TGroupModelPermission addPermission(TGroupModelPermission permission);

    /**
     * 分页查询权限列表
     * @param current 当前页
     * @param size 每页大小
     * @param groupId 分组ID（可选）
     * @param groupName 分组名称（可选）
     * @param modelId 模型ID（可选）
     * @param modelName 模型名称（可选）
     * @param visible 是否可见（可选）
     * @param callable 是否可调用（可选）
     * @return 分页结果
     */
    Page<TGroupModelPermission> queryPermissions(Integer current, Integer size,
                                                   Integer groupId, String groupName,
                                                   Integer modelId, String modelName,
                                                   Integer visible, Integer callable);

    /**
     * 删除权限
     * @param id 权限ID
     * @return 是否删除成功
     */
    boolean deletePermission(Integer id);

    /**
     * 修改权限
     * @param id 权限ID
     * @param permission 要修改的权限信息
     * @return 修改后的权限信息
     */
    TGroupModelPermission updatePermission(Integer id, TGroupModelPermission permission);
}
