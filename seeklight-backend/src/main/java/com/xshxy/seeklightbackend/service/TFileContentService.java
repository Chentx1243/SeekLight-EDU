package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TFileContent;
import org.springframework.web.multipart.MultipartFile;

public interface TFileContentService extends IService<TFileContent> {

    Integer parseFile(MultipartFile file);

    String parseFileContent(MultipartFile file);

    String getFileNameById(Integer fileId);
}
