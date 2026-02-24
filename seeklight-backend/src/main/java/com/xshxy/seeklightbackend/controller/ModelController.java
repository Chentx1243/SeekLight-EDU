package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TModel;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TModelService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "模型管理")
@RestController
@RequestMapping("/model")
public class ModelController {

    @Resource
    private TModelService modelService;

    @Resource
    private TGroupService groupService;

    @Resource
    private UserInfoService userInfoService;

    @PostMapping
    @Operation(summary = "新增模型", description = "新增模型并校验所属分组存在")
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
        modelService.save(model);
        return Result.success(model);
    }

    @GetMapping
    @Operation(summary = "查询模型", description = "支持按名称模糊匹配、状态和分组筛选")
    public Result<List<TModel>> listModels(
            @Parameter(description = "模型名称（模糊匹配）")
            @RequestParam(value = "modelName", required = false) String modelName,
            @Parameter(description = "模型状态：1上架，0下架")
            @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "分组ID")
            @RequestParam(value = "groupId", required = false) Integer groupId) {
        LambdaQueryWrapper<TModel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(modelName)) {
            queryWrapper.like(TModel::getModelName, modelName);
        }
        if (status != null) {
            queryWrapper.eq(TModel::getStatus, status);
        }
        queryWrapper.orderByDesc(TModel::getCreatedAt);
        return Result.success(modelService.list(queryWrapper));
    }


    @GetMapping("/available")
    @Operation(summary = "前台可用模型列表", description = "仅返回当前登录用户所在分组的模型")
    public Result<List<TModel>> listAvailableModels(
            @Parameter(description = "模型名称（模糊匹配）")
            @RequestParam(value = "modelName", required = false) String modelName,
            @Parameter(description = "模型状态：1上架，0下架")
            @RequestParam(value = "status", required = false) Integer status) {
        TUser user = userInfoService.getUser();
        if (user == null || user.getGroupId() == null) {
            throw new BusinessException("用户未登录");
        }
        LambdaQueryWrapper<TModel> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(modelName)) {
            queryWrapper.like(TModel::getModelName, modelName);
        }
        if (status != null) {
            queryWrapper.eq(TModel::getStatus, status);
        }
        queryWrapper.orderByDesc(TModel::getCreatedAt);
        return Result.success(modelService.list(queryWrapper));
    }


    @PutMapping("/{modelId}")
    @Operation(summary = "修改模型", description = "按模型ID更新模型信息")
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
    @Operation(summary = "删除模型", description = "按模型ID软删除模型")
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
