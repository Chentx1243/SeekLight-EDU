package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.config.MinioProperties;
import com.xshxy.seeklightbackend.domain.dto.ObjectStorageItemDto;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.KnowledgeBaseFileStorageService;
import com.xshxy.seeklightbackend.service.ObjectStorageService;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KnowledgeBaseFileStorageServiceImpl implements KnowledgeBaseFileStorageService {

    @Resource
    private ObjectStorageService objectStorageService;

    @Resource
    private MinioProperties minioProperties;

    @Override
    public void ensureBucketReady() {
        validateConfig();
        objectStorageService.ensureBucketExists(minioProperties.getBucket());
    }

    @Override
    public String upload(Integer kbId, MultipartFile file) {
        ensureBucketReady();
        if (kbId == null) {
            throw new BusinessException("Knowledge base ID cannot be null");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new BusinessException("File name cannot be empty");
        }
        String objectKey = buildObjectKey(kbId, originalFilename);
        try (InputStream inputStream = file.getInputStream()) {
            objectStorageService.uploadObject(
                    minioProperties.getBucket(),
                    objectKey,
                    inputStream,
                    file.getSize(),
                    file.getContentType() == null ? "application/octet-stream" : file.getContentType()
            );
            return objectKey;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to read upload file stream");
        }
    }

    @Override
    public InputStream getFile(String objectName) {
        ensureBucketReady();
        validateObjectName(objectName);
        return objectStorageService.getObject(minioProperties.getBucket(), objectName);
    }

    @Override
    public void delete(String objectName) {
        ensureBucketReady();
        validateObjectName(objectName);
        objectStorageService.removeObject(minioProperties.getBucket(), objectName);
    }

    @Override
    public boolean exists(String objectName) {
        ensureBucketReady();
        validateObjectName(objectName);
        return objectStorageService.objectExists(minioProperties.getBucket(), objectName);
    }

    @Override
    public List<ObjectStorageItemDto> list(Integer kbId) {
        ensureBucketReady();
        if (kbId == null) {
            throw new BusinessException("Knowledge base ID cannot be null");
        }
        return objectStorageService.listObjects(minioProperties.getBucket(), "kb/" + kbId + "/", true);
    }

    @Override
    public String buildObjectKey(Integer kbId, String fileName) {
        if (kbId == null) {
            throw new BusinessException("Knowledge base ID cannot be null");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException("File name cannot be empty");
        }
        String sanitizedName = fileName.replace("\\", "/");
        int lastSlashIndex = sanitizedName.lastIndexOf('/');
        if (lastSlashIndex >= 0) {
            sanitizedName = sanitizedName.substring(lastSlashIndex + 1);
        }
        sanitizedName = sanitizedName.replaceAll("[\\r\\n]", "_");
        return "kb/" + kbId + "/" + UUID.randomUUID() + "/" + sanitizedName;
    }

    private void validateConfig() {
        if (minioProperties.getEndpoint() == null || minioProperties.getEndpoint().isBlank()) {
            throw new BusinessException("MinIO endpoint is not configured");
        }
        if (minioProperties.getAccessKey() == null || minioProperties.getAccessKey().isBlank()) {
            throw new BusinessException("MinIO access key is not configured");
        }
        if (minioProperties.getSecretKey() == null || minioProperties.getSecretKey().isBlank()) {
            throw new BusinessException("MinIO secret key is not configured");
        }
        if (minioProperties.getBucket() == null || minioProperties.getBucket().isBlank()) {
            throw new BusinessException("MinIO bucket is not configured");
        }
    }

    private void validateObjectName(String objectName) {
        if (objectName == null || objectName.isBlank()) {
            throw new BusinessException("Object name cannot be empty");
        }
    }
}
