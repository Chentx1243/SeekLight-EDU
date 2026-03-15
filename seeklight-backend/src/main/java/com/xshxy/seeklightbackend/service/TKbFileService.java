package com.xshxy.seeklightbackend.service;

import com.xshxy.seeklightbackend.domain.TKbFile;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.InputStream;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
* @author 陈凯宁
* @description 针对表【t_kb_file(知识库文件表)】的数据库操作Service
* @createDate 2026-03-15 13:51:55
*/
public interface TKbFileService extends IService<TKbFile> {

    TKbFile uploadKbFile(Integer kbId, MultipartFile file);

    List<TKbFile> listKbFiles(Integer kbId);

    void deleteKbFile(Integer fileId);

    InputStream getKbFileStream(Integer fileId);
}
