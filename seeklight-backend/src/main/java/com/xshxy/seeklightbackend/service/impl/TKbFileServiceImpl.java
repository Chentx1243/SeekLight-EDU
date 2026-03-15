package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TKbFile;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TKbFileMapper;
import com.xshxy.seeklightbackend.service.*;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 陈凯宁
* @description 针对表【t_kb_file(知识库文件表)】的数据库操作Service实现
* @createDate 2026-03-15 13:51:55
*/
@Service
public class TKbFileServiceImpl extends ServiceImpl<TKbFileMapper, TKbFile>
    implements TKbFileService{

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private KnowledgeBaseFileStorageService knowledgeBaseFileStorageService;

    @Resource
    private FileRagAsyncService fileRagAsyncService;

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
        // 文件上传到对象存储
        String objectKey = knowledgeBaseFileStorageService.upload(kbId, file);
        TKbFile kbFile = new TKbFile();
        kbFile.setKbId(kbId);
        kbFile.setFileName(file.getOriginalFilename());
        kbFile.setFilePath(objectKey);
        kbFile.setFileSize(file.getSize());
        kbFile.setUploaderUserId(currentUser.getUserId());
        // 状态设为0：解析中
        kbFile.setStatus(0);
        kbFile.setIsDeleted(0);
        kbFile.setCreatedAt(new Date());
        kbFile.setUpdatedAt(new Date());
        save(kbFile);

        // 向量化存储
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
        TKbFile kbFile = getAvailableKbFile(fileId);
        knowledgeBaseFileStorageService.delete(kbFile.getFilePath());
        kbFile.setIsDeleted(1);
        kbFile.setUpdatedAt(new Date());
        updateById(kbFile);
    }

    @Override
    public InputStream getKbFileStream(Integer fileId) {
        TKbFile kbFile = getAvailableKbFile(fileId);
        return knowledgeBaseFileStorageService.getFile(kbFile.getFilePath());
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
}




