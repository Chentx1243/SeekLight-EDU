package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TModel;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TModelService;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/model")
public class ModelController {

    @Resource
    private TModelService modelService;

    @Resource
    private TGroupService groupService;

    @PostMapping
    public Result<TModel> createModel(@RequestBody TModel request) {
        if (request == null) {
            throw new BusinessException("模型信息不能为空");
        }
        if (!StringUtils.hasText(request.getModelName())) {
            throw new BusinessException("模型名称不能为空");
        }
        if (!StringUtils.hasText(request.getModelKey())) {
            throw new BusinessException("模型标识不能为空");
        }
        Integer groupId = request.getGroupId();
        if (groupId == null) {
            throw new BusinessException("分组ID不能为空");
        }
        TGroup group = groupService.getById(groupId);
        if (group == null) {
            throw new BusinessException("分组不存在");
        }
        Date now = new Date();
        TModel model = new TModel();
        model.setModelName(request.getModelName());
        model.setDescription(request.getDescription());
        model.setProvider(request.getProvider());
        model.setModelKey(request.getModelKey());
        model.setStatus(request.getStatus());
        model.setGroupId(groupId);
        model.setIsDeleted(0);
        model.setCreatedAt(now);
        model.setUpdatedAt(now);
        modelService.save(model);
        return Result.success(model);
    }

    @GetMapping
    public Result<List<TModel>> listModels(
            @RequestParam(value = "modelName", required = false) String modelName,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "groupId", required = false) Integer groupId) {
        LambdaQueryWrapper<TModel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(modelName)) {
            queryWrapper.like(TModel::getModelName, modelName);
        }
        if (status != null) {
            queryWrapper.eq(TModel::getStatus, status);
        }
        if (groupId != null) {
            queryWrapper.eq(TModel::getGroupId, groupId);
        }
        queryWrapper.orderByDesc(TModel::getCreatedAt);
        return Result.success(modelService.list(queryWrapper));
    }

    @PutMapping("/{modelId}")
    public Result<TModel> updateModel(@PathVariable Integer modelId, @RequestBody TModel request) {
        if (modelId == null) {
            throw new BusinessException("模型ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("模型信息不能为空");
        }
        TModel model = modelService.getById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        if (request.getGroupId() != null) {
            TGroup group = groupService.getById(request.getGroupId());
            if (group == null) {
                throw new BusinessException("分组不存在");
            }
            model.setGroupId(request.getGroupId());
        }
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
        modelService.updateById(model);
        return Result.success(model);
    }

    @DeleteMapping("/{modelId}")
    public Result<String> deleteModel(@PathVariable Integer modelId) {
        if (modelId == null) {
            throw new BusinessException("模型ID不能为空");
        }
        boolean removed = modelService.removeById(modelId);
        if (!removed) {
            return Result.failure("模型不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
