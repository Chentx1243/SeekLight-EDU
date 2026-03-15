package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.domain.dto.ObjectStorageItemDto;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.ObjectStorageService;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import jakarta.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MinioObjectStorageServiceImpl implements ObjectStorageService {

    @Resource
    private MinioClient minioClient;

    @Override
    public void ensureBucketExists(String bucketName) {
        try {
            if (!bucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            log.error("Failed to ensure MinIO bucket exists, bucket={}", bucketName, e);
            throw new BusinessException("MinIO bucket initialization failed");
        }
    }

    @Override
    public boolean bucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            log.error("Failed to check MinIO bucket, bucket={}", bucketName, e);
            throw new BusinessException("Failed to check MinIO bucket");
        }
    }

    @Override
    public void uploadObject(String bucketName, String objectName, InputStream inputStream, long objectSize, String contentType) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, objectSize, -1)
                    .contentType(contentType)
                    .build());
        } catch (Exception e) {
            log.error("Failed to upload object to MinIO, bucket={}, object={}", bucketName, objectName, e);
            throw new BusinessException("Failed to upload file to object storage");
        }
    }

    @Override
    public InputStream getObject(String bucketName, String objectName) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("Failed to fetch object from MinIO, bucket={}, object={}", bucketName, objectName, e);
            throw new BusinessException("Failed to read file from object storage");
        }
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
        } catch (Exception e) {
            log.error("Failed to delete object from MinIO, bucket={}, object={}", bucketName, objectName, e);
            throw new BusinessException("Failed to delete file from object storage");
        }
    }

    @Override
    public boolean objectExists(String bucketName, String objectName) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build());
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse() != null && "NoSuchKey".equals(e.errorResponse().code())) {
                return false;
            }
            log.error("Failed to stat object in MinIO, bucket={}, object={}", bucketName, objectName, e);
            throw new BusinessException("Failed to query file in object storage");
        } catch (Exception e) {
            log.error("Failed to stat object in MinIO, bucket={}, object={}", bucketName, objectName, e);
            throw new BusinessException("Failed to query file in object storage");
        }
    }

    @Override
    public List<ObjectStorageItemDto> listObjects(String bucketName, String prefix, boolean recursive) {
        try {
            Iterable<io.minio.Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .prefix(prefix)
                    .recursive(recursive)
                    .build());
            List<ObjectStorageItemDto> objects = new ArrayList<>();
            for (io.minio.Result<Item> result : results) {
                Item item = result.get();
                if (!item.isDir()) {
                    objects.add(new ObjectStorageItemDto(
                            item.objectName(),
                            item.size(),
                            item.etag(),
                            item.lastModified()
                    ));
                }
            }
            return objects;
        } catch (Exception e) {
            log.error("Failed to list objects in MinIO, bucket={}, prefix={}", bucketName, prefix, e);
            throw new BusinessException("Failed to list files from object storage");
        }
    }
}
