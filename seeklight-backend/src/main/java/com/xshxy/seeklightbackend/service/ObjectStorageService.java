package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.dto.ObjectStorageItemDto;
import java.io.InputStream;
import java.util.List;

public interface ObjectStorageService {

    void ensureBucketExists(String bucketName);

    boolean bucketExists(String bucketName);

    void uploadObject(String bucketName, String objectName, InputStream inputStream, long objectSize, String contentType);

    InputStream getObject(String bucketName, String objectName);

    void removeObject(String bucketName, String objectName);

    boolean objectExists(String bucketName, String objectName);

    List<ObjectStorageItemDto> listObjects(String bucketName, String prefix, boolean recursive);
}
