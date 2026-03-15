package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TGroupProviderCredential;
import com.xshxy.seeklightbackend.domain.dto.GroupProviderCredentialListItemDto;
import com.xshxy.seeklightbackend.domain.request.CreateGroupProviderCredentialRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateGroupProviderCredentialRequest;
import com.xshxy.seeklightbackend.service.TGroupProviderCredentialService;
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

@Tag(name = "分组供应商凭据管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/group-provider-credential")
public class GroupProviderCredentialController {

    @Resource
    private TGroupProviderCredentialService credentialService;

    @GetMapping
    @Operation(summary = "查询凭据列表", description = "分页查询分组供应商凭据配置")
    public Result<Page<GroupProviderCredentialListItemDto>> listCredentials(
            @Parameter(description = "当前页码")
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页大小")
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "分组 ID")
            @RequestParam(value = "groupId", required = false) Integer groupId,
            @Parameter(description = "供应商 ID")
            @RequestParam(value = "providerId", required = false) Integer providerId) {
        return Result.success(credentialService.pageCredentials(current, size, groupId, providerId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询凭据详情", description = "根据 ID 查询分组供应商凭据配置")
    public Result<TGroupProviderCredential> getCredential(@PathVariable Integer id) {
        return Result.success(credentialService.getCredential(id));
    }

    @PostMapping
    @Operation(summary = "新增凭据配置", description = "新增分组供应商凭据配置")
    public Result<TGroupProviderCredential> createCredential(@RequestBody CreateGroupProviderCredentialRequest request) {
        return Result.success(credentialService.createCredential(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改凭据配置", description = "根据 ID 修改分组供应商凭据配置")
    public Result<TGroupProviderCredential> updateCredential(
            @PathVariable Integer id,
            @RequestBody UpdateGroupProviderCredentialRequest request) {
        return Result.success(credentialService.updateCredential(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除凭据配置", description = "根据 ID 删除分组供应商凭据配置")
    public Result<String> deleteCredential(@PathVariable Integer id) {
        boolean removed = credentialService.deleteCredential(id);
        if (!removed) {
            return Result.failure("删除失败");
        }
        return Result.success("删除成功");
    }
}
