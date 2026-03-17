package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.constant.SecurityRoles;
import com.xshxy.seeklightbackend.domain.TKbFile;
import com.xshxy.seeklightbackend.domain.TKnowledgeBase;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TKnowledgeBaseMapper;
import com.xshxy.seeklightbackend.service.KnowledgeBaseFileStorageService;
import com.xshxy.seeklightbackend.service.TKbFileService;
import com.xshxy.seeklightbackend.service.TKnowledgeBaseService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Slf4j
@Service
public class TKnowledgeBaseServiceImpl extends ServiceImpl<TKnowledgeBaseMapper, TKnowledgeBase>
    implements TKnowledgeBaseService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TKbFileService kbFileService;

    @Resource
    private KnowledgeBaseFileStorageService knowledgeBaseFileStorageService;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Override
    public TKnowledgeBase createKnowledgeBase(TKnowledgeBase request) {
        if (request == null) {
            throw new BusinessException("知识库信息不能为空");
        }
        if (!StringUtils.hasText(request.getKbName())) {
            throw new BusinessException("知识库名称不能为空");
        }
        TUser currentUser = getCurrentUser();
        Date now = new Date();

        TKnowledgeBase knowledgeBase = new TKnowledgeBase();
        knowledgeBase.setKbName(request.getKbName().trim());
        knowledgeBase.setDescription(request.getDescription());
        knowledgeBase.setOwnerUserId(currentUser.getUserId());
        knowledgeBase.setOwnerGroupId(currentUser.getGroupId());
        knowledgeBase.setIsGroupShared(request.getIsGroupShared() == null ? 0 : request.getIsGroupShared());
        knowledgeBase.setIsDeleted(0);
        knowledgeBase.setCreatedAt(now);
        knowledgeBase.setUpdatedAt(now);
        save(knowledgeBase);
        return knowledgeBase;
    }

    @Override
    public Page<TKnowledgeBase> pageKnowledgeBases(long current, long size, String kbName) {
        if (current <= 0) {
            throw new BusinessException("页码必须大于0");
        }
        if (size <= 0) {
            throw new BusinessException("每页条数必须大于0");
        }
        TUser currentUser = getCurrentUser();
        LambdaQueryWrapper<TKnowledgeBase> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(kbName)) {
            queryWrapper.like(TKnowledgeBase::getKbName, kbName.trim());
        }
        if (!isAdmin(currentUser)) {
            queryWrapper.and(wrapper -> wrapper
                    .eq(TKnowledgeBase::getOwnerUserId, currentUser.getUserId())
                    .or(sharedWrapper -> sharedWrapper
                            .eq(TKnowledgeBase::getOwnerGroupId, currentUser.getGroupId())
                            .eq(TKnowledgeBase::getIsGroupShared, 1)));
        }
        queryWrapper.orderByDesc(TKnowledgeBase::getCreatedAt);
        return page(new Page<>(current, size), queryWrapper);
    }

    @Override
    public TKnowledgeBase getKnowledgeBaseDetail(Integer kbId) {
        TKnowledgeBase knowledgeBase = getExistingKnowledgeBase(kbId);
        validateAccess(knowledgeBase);
        return knowledgeBase;
    }

    @Override
    public TKnowledgeBase updateKnowledgeBase(Integer kbId, TKnowledgeBase request) {
        if (request == null) {
            throw new BusinessException("知识库信息不能为空");
        }
        TKnowledgeBase knowledgeBase = getExistingKnowledgeBase(kbId);
        validateOwnerOrAdmin(knowledgeBase);

        if (request.getKbName() != null) {
            if (!StringUtils.hasText(request.getKbName())) {
                throw new BusinessException("知识库名称不能为空");
            }
            knowledgeBase.setKbName(request.getKbName().trim());
        }
        if (request.getDescription() != null) {
            knowledgeBase.setDescription(request.getDescription());
        }
        if (request.getIsGroupShared() != null) {
            knowledgeBase.setIsGroupShared(request.getIsGroupShared());
        }
        knowledgeBase.setUpdatedAt(new Date());
        updateById(knowledgeBase);
        return knowledgeBase;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteKnowledgeBase(Integer kbId) {
        TKnowledgeBase knowledgeBase = getExistingKnowledgeBase(kbId);
        validateOwnerOrAdmin(knowledgeBase);
        deleteKnowledgeBaseEmbeddings(kbId);
        deleteKnowledgeBaseFiles(knowledgeBase.getKbId());
        return removeById(kbId);
    }

    private void deleteKnowledgeBaseEmbeddings(Integer kbId) {
        try {
            embeddingStore.removeAll(metadataKey("kb_id").isEqualTo(kbId));
        } catch (Exception e) {
            log.error("Failed to delete embeddings for knowledge base {}", kbId, e);
            throw new BusinessException("删除知识库向量数据失败");
        }
    }

    private void deleteKnowledgeBaseFiles(Integer kbId) {
        List<TKbFile> kbFiles = kbFileService.listKbFiles(kbId);
        for (TKbFile kbFile : kbFiles) {
            if (StringUtils.hasText(kbFile.getFilePath())) {
                knowledgeBaseFileStorageService.delete(kbFile.getFilePath());
            }
        }
        List<Integer> fileIds = kbFiles.stream()
                .map(TKbFile::getFileId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (!fileIds.isEmpty()) {
            kbFileService.removeByIds(fileIds);
        }
    }

    private TKnowledgeBase getExistingKnowledgeBase(Integer kbId) {
        if (kbId == null) {
            throw new BusinessException("知识库ID不能为空");
        }
        TKnowledgeBase knowledgeBase = getById(kbId);
        if (knowledgeBase == null) {
            throw new BusinessException("知识库不存在");
        }
        return knowledgeBase;
    }

    private TUser getCurrentUser() {
        TUser currentUser = userInfoService.getUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        return currentUser;
    }

    private void validateAccess(TKnowledgeBase knowledgeBase) {
        TUser currentUser = getCurrentUser();
        if (isAdmin(currentUser)) {
            return;
        }
        boolean isOwner = currentUser.getUserId().equals(knowledgeBase.getOwnerUserId());
        boolean isSharedToGroup = Integer.valueOf(1).equals(knowledgeBase.getIsGroupShared())
                && currentUser.getGroupId() != null
                && currentUser.getGroupId().equals(knowledgeBase.getOwnerGroupId());
        if (!isOwner && !isSharedToGroup) {
            throw new BusinessException("没有权限访问该知识库");
        }
    }

    private void validateOwnerOrAdmin(TKnowledgeBase knowledgeBase) {
        TUser currentUser = getCurrentUser();
        if (isAdmin(currentUser)) {
            return;
        }
        if (!currentUser.getUserId().equals(knowledgeBase.getOwnerUserId())) {
            throw new BusinessException("没有权限操作该知识库");
        }
    }

    private boolean isAdmin(TUser user) {
        return SecurityRoles.ADMIN.equals(SecurityRoles.normalize(user.getRole()));
    }
}
