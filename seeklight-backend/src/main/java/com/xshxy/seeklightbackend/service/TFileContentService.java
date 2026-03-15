package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TFileContent;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;

public interface TFileContentService extends IService<TFileContent> {

    Integer parseFile(MultipartFile file);

    String parseFileContent(MultipartFile file);

    String parseFileContent(InputStream inputStream, String fileType);

    String getFileNameById(Integer fileId);
}
