package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TModel;
import com.xshxy.seeklightbackend.domain.dto.ModelDto;
import com.xshxy.seeklightbackend.service.TModelService;
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

import java.util.List;

@Tag(name = "模型管理")
@RestController
@RequestMapping("/model")
public class ModelController {

    @Resource
    private TModelService modelService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "新增模型", description = "新增模型并校验所属分组存在")
    public Result<TModel> createModel(@RequestBody TModel request) {
        TModel model = modelService.createModelWithValidation(request);
        return Result.success(model);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "查询模型", description = "管理员查询模型列表，支持按名称、状态和分组筛选")
    public Result<List<TModel>> listModels(
            @Parameter(description = "模型名称，模糊匹配")
            @RequestParam(value = "modelName", required = false) String modelName,
            @Parameter(description = "模型状态，1 上架，0 下架")
            @RequestParam(value = "status", required = false) Integer status,
            @Parameter(description = "分组 ID")
            @RequestParam(value = "groupId", required = false) Integer groupId) {
        List<TModel> models = modelService.listModelsByCondition(modelName, status, groupId);
        return Result.success(models);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/available")
    @Operation(summary = "后台可用模型列表", description = "查看符合条件的可用模型")
    public Result<List<ModelDto>> listAvailableModels(
            @Parameter(description = "模型名称，模糊匹配")
            @RequestParam(value = "modelName", required = false) String modelName,
            @Parameter(description = "模型状态，1 上架，0 下架")
            @RequestParam(value = "status", required = false) Integer status) {
        List<ModelDto> models = modelService.listAvailableModelsForUser(modelName, status);
        return Result.success(models);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{modelId}")
    @Operation(summary = "修改模型", description = "按模型 ID 更新模型信息")
    public Result<TModel> updateModel(
            @PathVariable Integer modelId,
            @RequestBody TModel request) {
        TModel model = modelService.updateModelWithValidation(modelId, request);
        return Result.success(model);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{modelId}")
    @Operation(summary = "删除模型", description = "按模型 ID 软删除模型")
    public Result<String> deleteModel(@PathVariable Integer modelId) {
        boolean removed = modelService.deleteModelWithValidation(modelId);
        if (!removed) {
            return Result.failure("模型不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
