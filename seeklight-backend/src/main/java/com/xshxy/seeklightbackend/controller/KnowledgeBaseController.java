package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TKbFile;
import com.xshxy.seeklightbackend.domain.TKnowledgeBase;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.request.BatchDeleteKbFilesRequest;
import com.xshxy.seeklightbackend.domain.request.CreateKnowledgeBaseRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TKbFileService;
import com.xshxy.seeklightbackend.service.TKnowledgeBaseService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "知识库管理")
@RestController
@RequestMapping("/knowledge-base")
public class KnowledgeBaseController {

    @Resource
    private TKnowledgeBaseService knowledgeBaseService;

    @Resource
    private TKbFileService kbFileService;

    @Resource
    private UserInfoService userInfoService;

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
            @Parameter(description = "页码，从 1 开始")
            @RequestParam(value = "current", defaultValue = "1") Long current,
            @Parameter(description = "每页条数")
            @RequestParam(value = "size", defaultValue = "10") Long size,
            @Parameter(description = "知识库名称，模糊匹配")
            @RequestParam(value = "kbName", required = false) String kbName) {
        return Result.success(knowledgeBaseService.pageKnowledgeBases(current, size, kbName));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{kbId}")
    @Operation(summary = "知识库详情", description = "根据知识库 ID 查询详情")
    public Result<TKnowledgeBase> getKnowledgeBase(@PathVariable Integer kbId) {
        return Result.success(knowledgeBaseService.getKnowledgeBaseDetail(kbId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{kbId}/files")
    @Operation(summary = "查询知识库文件列表", description = "根据知识库 ID 查询当前用户可访问的知识库文件列表，不分页")
    public Result<List<TKbFile>> listKnowledgeBaseFiles(@PathVariable Integer kbId) {
        knowledgeBaseService.getKnowledgeBaseDetail(kbId);
        return Result.success(kbFileService.listKbFiles(kbId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping(value = "/{kbId}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传知识库文件", description = "校验知识库存在且属于当前登录用户后上传文件")
    public Result<TKbFile> uploadKnowledgeBaseFile(
            @PathVariable Integer kbId,
            @RequestParam("file") MultipartFile file) {
        TKnowledgeBase knowledgeBase = knowledgeBaseService.getById(kbId);
        if (knowledgeBase == null) {
            throw new BusinessException("Knowledge base does not exist");
        }

        TUser currentUser = userInfoService.getUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("User is not logged in");
        }

        if (!currentUser.getUserId().equals(knowledgeBase.getOwnerUserId())) {
            throw new BusinessException("No permission to upload files to this knowledge base");
        }

        return Result.success(kbFileService.uploadKbFile(kbId, file));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/files")
    @Operation(summary = "批量删除知识库文件", description = "根据文件 ID 列表批量删除知识库文件")
    public Result<String> batchDeleteKnowledgeBaseFiles(@RequestBody BatchDeleteKbFilesRequest request) {
        kbFileService.batchDeleteKbFiles(request == null ? null : request.getFileIds());
        return Result.success("删除成功");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{kbId}")
    @Operation(summary = "修改知识库", description = "根据知识库 ID 更新知识库信息")
    public Result<TKnowledgeBase> updateKnowledgeBase(
            @PathVariable Integer kbId,
            @RequestBody TKnowledgeBase request) {
        return Result.success(knowledgeBaseService.updateKnowledgeBase(kbId, request));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping("/{kbId}")
    @Operation(summary = "删除知识库", description = "根据知识库 ID 删除知识库")
    public Result<String> deleteKnowledgeBase(@PathVariable Integer kbId) {
        boolean removed = knowledgeBaseService.deleteKnowledgeBase(kbId);
        if (!removed) {
            return Result.failure("知识库不存在或已删除");
        }
        return Result.success("删除成功");
    }
}
