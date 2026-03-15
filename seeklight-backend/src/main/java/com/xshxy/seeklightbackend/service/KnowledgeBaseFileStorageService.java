package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.dto.ObjectStorageItemDto;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface KnowledgeBaseFileStorageService {

    String upload(Integer kbId, MultipartFile file);

    InputStream getFile(String objectName);

    void delete(String objectName);

    boolean exists(String objectName);

    List<ObjectStorageItemDto> list(Integer kbId);

    String buildObjectKey(Integer kbId, String fileName);

    void ensureBucketReady();
}
