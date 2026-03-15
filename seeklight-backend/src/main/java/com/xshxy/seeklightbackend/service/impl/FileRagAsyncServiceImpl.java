package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.domain.TKbFile;
import com.xshxy.seeklightbackend.mapper.TKbFileMapper;
import com.xshxy.seeklightbackend.service.FileRagAsyncService;
import com.xshxy.seeklightbackend.service.KnowledgeBaseFileStorageService;
import com.xshxy.seeklightbackend.service.TFileContentService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.InputStream;


@Slf4j
@Service
public class FileRagAsyncServiceImpl implements FileRagAsyncService {

    @Resource
    private KnowledgeBaseFileStorageService knowledgeBaseFileStorageService;

    @Resource
    private TFileContentService fileContentService;

    @Resource
    private EmbeddingStoreIngestor embeddingStoreIngestor;

    @Resource
    private TKbFileMapper fileMapper;

    @Override
    @Async
    public void ragStore(TKbFile fileInfo, String objectKey) {
        try {
            // 从对象存储中拿出文件流
            InputStream fileStream = knowledgeBaseFileStorageService.getFile(objectKey);
            // 获取文件内容
            String fileContent = fileContentService.parseFileContent(fileStream, fileInfo.getFileName());
            // 载入文档对象（文件内容+Meta信息）
            Document document = initDocument(fileInfo,fileContent);
            // 使用切片器切片文档
            IngestionResult ingestionResult = embeddingStoreIngestor.ingest(document);
            // 文件状态设为可用
            fileInfo.setStatus(1);
            fileMapper.updateById(fileInfo);
        }catch (Exception e){
            log.error("向量化存储失败：" + e.getMessage());
            fileInfo.setStatus(2);
            fileMapper.updateById(fileInfo);
        }

    }

    private Document initDocument(TKbFile fileInfo, String fileContent) {
        Metadata metadata = new Metadata();
        metadata.put("file_id",fileInfo.getFileId());
        metadata.put("kb_id",fileInfo.getKbId());
        metadata.put("uploader_user_id",fileInfo.getUploaderUserId());
        metadata.put("file_name",fileInfo.getFileName());
        return Document.from(fileContent,metadata);
    }

}
