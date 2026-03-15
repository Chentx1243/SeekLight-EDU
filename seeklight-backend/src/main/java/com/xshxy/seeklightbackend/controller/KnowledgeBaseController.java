package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TKnowledgeBase;
import com.xshxy.seeklightbackend.domain.request.CreateKnowledgeBaseRequest;
import com.xshxy.seeklightbackend.service.TKnowledgeBaseService;
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

@Tag(name = "知识库管理")
@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeBaseController {

    @Resource
    private TKnowledgeBaseService knowledgeBaseService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    @Operation(summary = "新增知识库", description = "创建一个新的知识库")
    public Result<TKnowledgeBase> createKnowledgeBase(@RequestBody CreateKnowledgeBaseRequest request) {
        TKnowledgeBase knowledgeBase = new TKnowledgeBase();
        knowledgeBase.setKbName(request.getKbName());
        knowledgeBase.setDescription(request.getDescription());
        knowledgeBase.setIsGroupShared(request.getIsGroupShared());
        return Result.success(knowledgeBaseService.createKnowledgeBase(knowledgeBase));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    @Operation(summary = "分页查询知识库", description = "分页查询当前用户可访问的知识库")
    public Result<Page<TKnowledgeBase>> pageKnowledgeBases(
            @Parameter(description = "页码，从1开始")
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @Parameter(description = "每页条数")
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @Parameter(description = "知识库名称，模糊匹配")
            @RequestParam(value = "kbName", required = false) String kbName) {
        return Result.success(knowledgeBaseService.pageKnowledgeBases(current, size, kbName));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{kbId}")
    @Operation(summary = "知识库详情", description = "根据知识库ID查询详情")
    public Result<TKnowledgeBase> getKnowledgeBase(@PathVariable Integer kbId) {
        return Result.success(knowledgeBaseService.getKnowledgeBaseDetail(kbId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{kbId}")
    @Operation(summary = "修改知识库", description = "根据知识库ID更新知识库信息")
    public Result<TKnowledgeBase> updateKnowledgeBase(
            @PathVariable Integer kbId,
            @RequestBody TKnowledgeBase request) {
        return Result.success(knowledgeBaseService.updateKnowledgeBase(kbId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{kbId}")
    @Operation(summary = "删除知识库", description = "根据知识库ID删除知识库")
    public Result<String> deleteKnowledgeBase(@PathVariable Integer kbId) {
        boolean removed = knowledgeBaseService.deleteKnowledgeBase(kbId);
        if (!removed) {
            return Result.failure("知识库不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
