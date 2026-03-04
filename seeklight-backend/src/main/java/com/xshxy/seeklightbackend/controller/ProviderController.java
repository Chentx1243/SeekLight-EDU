package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TModelProvider;
import com.xshxy.seeklightbackend.domain.request.ProviderRequest;
import com.xshxy.seeklightbackend.service.TModelProviderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "供应商管理")
@RestController
@RequestMapping("/provider")
public class ProviderController {

    @Resource
    private TModelProviderService modelProviderService;

    @PostMapping
    @Operation(summary = "新增供应商", description = "新增模型供应商并校验所属模型存在")
    public Result<TModelProvider> createProvider(@RequestBody ProviderRequest request) {
        TModelProvider provider = modelProviderService.createProvider(request);
        return Result.success(provider);
    }

    @GetMapping
    @Operation(summary = "查询供应商列表", description = "支持按名称模糊匹配、状态和模型ID筛选")
    public Result<List<TModelProvider>> listProviders(
            @Parameter(description = "供应商名称（模糊匹配）")
            @RequestParam(value = "providerName", required = false) String providerName,
            @Parameter(description = "供应商状态：1启用，0禁用")
            @RequestParam(value = "status", required = false) Integer status) {
        List<TModelProvider> providers = modelProviderService.listProviders(providerName, status);
        return Result.success(providers);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询单个供应商", description = "根据ID查询供应商详细信息")
    public Result<TModelProvider> getProvider(@PathVariable Integer id) {
        TModelProvider provider = modelProviderService.getProviderById(id);
        return Result.success(provider);
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改供应商", description = "按供应商ID更新供应商信息")
    public Result<TModelProvider> updateProvider(@PathVariable Integer id, @RequestBody ProviderRequest request) {
        TModelProvider provider = modelProviderService.updateProvider(id, request);
        return Result.success(provider);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商", description = "按供应商ID删除供应商")
    public Result<String> deleteProvider(@PathVariable Integer id) {
        boolean removed = modelProviderService.deleteProvider(id);
        if (!removed) {
            return Result.failure("供应商不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
