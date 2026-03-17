package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.constant.SecurityRoles;
import com.xshxy.seeklightbackend.domain.TKbFile;
import com.xshxy.seeklightbackend.domain.TKnowledgeBase;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TKbFileMapper;
import com.xshxy.seeklightbackend.mapper.TKnowledgeBaseMapper;
import com.xshxy.seeklightbackend.service.FileRagAsyncService;
import com.xshxy.seeklightbackend.service.KnowledgeBaseFileStorageService;
import com.xshxy.seeklightbackend.service.TKbFileService;
import com.xshxy.seeklightbackend.service.TKnowledgeBaseService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

@Slf4j
@Service
public class TKbFileServiceImpl extends ServiceImpl<TKbFileMapper, TKbFile>
    implements TKbFileService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private KnowledgeBaseFileStorageService knowledgeBaseFileStorageService;

    @Resource
    private FileRagAsyncService fileRagAsyncService;

    @Resource
    private TKnowledgeBaseMapper knowledgeBaseMapper;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TKbFile uploadKbFile(Integer kbId, MultipartFile file) {
        if (kbId == null) {
            throw new BusinessException("知识库ID不能为空");
        }
        TUser currentUser = userInfoService.getUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        String objectKey = knowledgeBaseFileStorageService.upload(kbId, file);
        TKbFile kbFile = new TKbFile();
        kbFile.setKbId(kbId);
        kbFile.setFileName(file.getOriginalFilename());
        kbFile.setFilePath(objectKey);
        kbFile.setFileSize(file.getSize());
        kbFile.setUploaderUserId(currentUser.getUserId());
        kbFile.setStatus(0);
        kbFile.setIsDeleted(0);
        kbFile.setCreatedAt(new Date());
        kbFile.setUpdatedAt(new Date());
        save(kbFile);
        fileRagAsyncService.ragStore(kbFile, objectKey);
        return kbFile;
    }

    @Override
    public List<TKbFile> listKbFiles(Integer kbId) {
        if (kbId == null) {
            throw new BusinessException("知识库ID不能为空");
        }
        return list(new LambdaQueryWrapper<TKbFile>()
                .eq(TKbFile::getKbId, kbId)
                .eq(TKbFile::getIsDeleted, 0)
                .orderByDesc(TKbFile::getCreatedAt));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteKbFile(Integer fileId) {
        batchDeleteKbFiles(List.of(fileId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteKbFiles(List<Integer> fileIds) {
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new BusinessException("文件ID列表不能为空");
        }
        Set<Integer> distinctFileIds = new LinkedHashSet<>(fileIds);
        for (Integer fileId : distinctFileIds) {
            deleteSingleKbFile(fileId);
        }
    }

    @Override
    public InputStream getKbFileStream(Integer fileId) {
        TKbFile kbFile = getAvailableKbFile(fileId);
        return knowledgeBaseFileStorageService.getFile(kbFile.getFilePath());
    }

    private void deleteSingleKbFile(Integer fileId) {
        TKbFile kbFile = getAvailableKbFile(fileId);
        validateDeletePermission(kbFile.getKbId());
        // 删除向量库
        deleteFileEmbeddings(fileId);
        // 删除对象存储
        if (StringUtils.hasText(kbFile.getFilePath())) {
            knowledgeBaseFileStorageService.delete(kbFile.getFilePath());
        }
        removeById(fileId);
    }

    private void validateDeletePermission(Integer kbId) {
        TKnowledgeBase knowledgeBase = knowledgeBaseMapper.selectById(kbId);
        if (knowledgeBase == null) {
            throw new BusinessException("知识库不存在");
        }
        TUser currentUser = userInfoService.getUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        if (isAdmin(currentUser)) {
            return;
        }
        if (!currentUser.getUserId().equals(knowledgeBase.getOwnerUserId())) {
            throw new BusinessException("没有权限删除该知识库文件");
        }
    }

    private void deleteFileEmbeddings(Integer fileId) {
        try {
            embeddingStore.removeAll(metadataKey("file_id").isEqualTo(fileId));
        } catch (Exception e) {
            log.error("Failed to delete embeddings for file {}", fileId, e);
            throw new BusinessException("删除知识库文件向量数据失败");
        }
    }

    private TKbFile getAvailableKbFile(Integer fileId) {
        if (fileId == null) {
            throw new BusinessException("文件ID不能为空");
        }
        TKbFile kbFile = getById(fileId);
        if (kbFile == null || Integer.valueOf(1).equals(kbFile.getIsDeleted())) {
            throw new BusinessException("知识库文件不存在");
        }
        return kbFile;
    }

    private boolean isAdmin(TUser user) {
        return SecurityRoles.ADMIN.equals(SecurityRoles.normalize(user.getRole()));
    }
}
