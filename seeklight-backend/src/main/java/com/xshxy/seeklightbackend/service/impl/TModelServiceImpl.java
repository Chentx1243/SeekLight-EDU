package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.*;
import com.xshxy.seeklightbackend.domain.dto.ModelDto;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.*;
import com.xshxy.seeklightbackend.mapper.TModelMapper;
import jakarta.annotation.Resource;
import net.sf.jsqlparser.Model;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_model(可用模型表)】的数据库操作Service实现
* @createDate 2025-08-30 16:41:28
*/
@Service
public class TModelServiceImpl extends ServiceImpl<TModelMapper, TModel>
    implements TModelService{

    @Resource
    private TModelProviderService providerService;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TGroupService groupService;

    @Resource
    private TGroupModelPermissionService permissionService;

    @Override
    public TModel createModelWithValidation(TModel request) {
        // 参数校验
        if (request == null) {
            throw new BusinessException("模型信息不能为空");
        }
        if (!StringUtils.hasText(request.getModelName())) {
            throw new BusinessException("模型名称不能为空");
        }
        if (!StringUtils.hasText(request.getModelKey())) {
            throw new BusinessException("模型标识不能为空");
        }
        if (request.getProvider() == null) {
            throw new BusinessException("请提供模型供应商编号");
        }

        // 校验供应商存在
        TModelProvider provider = providerService.getById(request.getProvider());
        if (provider == null) {
            throw new BusinessException("模型供应商不存在");
        }

        // 构建模型对象
        Date now = new Date();
        TModel model = new TModel();
        model.setModelName(request.getModelName());
        model.setDescription(request.getDescription());
        model.setProvider(request.getProvider());
        model.setModelKey(request.getModelKey());
        model.setStatus(request.getStatus());
        model.setIsDeleted(0);
        model.setCreatedAt(now);
        model.setUpdatedAt(now);

        // 保存到数据库
        save(model);
        return model;
    }

    @Override
    public List<TModel> listModelsByCondition(String modelName, Integer status, Integer groupId) {
        LambdaQueryWrapper<TModel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(modelName)) {
            queryWrapper.like(TModel::getModelName, modelName);
        }
        if (status != null) {
            queryWrapper.eq(TModel::getStatus, status);
        }
        queryWrapper.orderByDesc(TModel::getCreatedAt);
        return list(queryWrapper);
    }

    @Override
    public List<ModelDto> listAvailableModelsForUser(String modelName, Integer status) {
        // 获取当前登录用户
        TUser user = userInfoService.getUser();
        if (user == null || user.getGroupId() == null) {
            throw new BusinessException("用户未登录");
        }
        // 获取当前用户的权限分组
        TGroup userGroup = groupService.getById(user.getGroupId());
        if (userGroup == null) {
            throw new BusinessException("用户权限分组不存在");
        }
        // 查询分组可用的模型
        LambdaQueryWrapper<TGroupModelPermission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.eq(TGroupModelPermission::getGroupId, userGroup.getGroupId());
        List<TGroupModelPermission> modelPermissions = permissionService.list(permissionWrapper);
        // 可用模型的模型id列表
        List<Integer> modelIds = modelPermissions.stream().map(TGroupModelPermission::getModelId).toList();
        // 根据id列表获取模型信息，并转化为dto
        LambdaQueryWrapper<TModel> modelWrapper = new LambdaQueryWrapper<>();
        modelWrapper.in(TModel::getModelId, modelIds);
        List<TModel> modelList = this.list(modelWrapper);
        // 遍历模型集合，获取供应商信息
        ArrayList<ModelDto> modelDtos = new ArrayList<>();
        for (TModel model : modelList) {
            TModelProvider provider = providerService.getById(model.getProvider());
            ModelDto modelDto = new ModelDto();
            BeanUtils.copyProperties(model, modelDto);
            modelDto.setProvider(provider.getProviderName());
            modelDtos.add(modelDto);
        }
        return modelDtos;
    }

    @Override
    public TModel updateModelWithValidation(Integer modelId, TModel request) {
        // 参数校验
        if (modelId == null) {
            throw new BusinessException("模型ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("模型信息不能为空");
        }

        // 查询模型是否存在
        TModel model = getById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }

        // 更新字段
        if (request.getModelName() != null) {
            if (!StringUtils.hasText(request.getModelName())) {
                throw new BusinessException("模型名称不能为空");
            }
            model.setModelName(request.getModelName());
        }
        if (request.getDescription() != null) {
            model.setDescription(request.getDescription());
        }
        if (request.getProvider() != null) {
            model.setProvider(request.getProvider());
        }
        if (request.getModelKey() != null) {
            if (!StringUtils.hasText(request.getModelKey())) {
                throw new BusinessException("模型标识不能为空");
            }
            model.setModelKey(request.getModelKey());
        }
        if (request.getStatus() != null) {
            model.setStatus(request.getStatus());
        }
        model.setUpdatedAt(new Date());

        // 更新到数据库
        updateById(model);
        return model;
    }

    @Override
    public boolean deleteModelWithValidation(Integer modelId) {
        if (modelId == null) {
            throw new BusinessException("模型ID不能为空");
        }
        return removeById(modelId);
    }
}




