package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TFileContent;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.service.TFileContentService;
import com.xshxy.seeklightbackend.mapper.TFileContentMapper;
import com.xshxy.seeklightbackend.service.UserInfoService;
import com.xshxy.seeklightbackend.util.DocumentParseUtil;
import jakarta.annotation.Resource;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
* @author 陈凯宁
* @description 针对表【t_file_content(文件内容解析表)】的数据库操作Service实现
* @createDate 2026-03-07 17:42:58
*/
@Service
public class TFileContentServiceImpl extends ServiceImpl<TFileContentMapper, TFileContent>
    implements TFileContentService{

    @Resource
    private UserInfoService userInfoService;

    @Override
    public Integer parseFile(MultipartFile file) {
        if (file == null || file.isEmpty()){
            throw new BusinessException("文件不能为空");
        }
        // 获取用户信息
        TUser user = userInfoService.getUser();
        // 获取文件完整名称
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()){
            throw new BusinessException("文件名不能为空");
        }
        // 获取文件后缀
        String lowerFileName = fileName.toLowerCase();
        // 文件内容
        String fileContent = "";
        try{
            if (lowerFileName.endsWith(".pdf")) {
                // 解析文件内容
                fileContent = DocumentParseUtil.parsePdf(file);
            }else if (lowerFileName.endsWith(".docx")) {
                fileContent = DocumentParseUtil.parseWord(file);
            }else if (lowerFileName.endsWith(".xlsx")) {
                fileContent = DocumentParseUtil.parseExcel(file);
            }else if (lowerFileName.endsWith(".txt")) {
                fileContent = DocumentParseUtil.parseTxt(file);
            }else {
                throw new BusinessException("不支持的文件类型");
            }
            TFileContent parsedFile = new TFileContent();
            parsedFile.setFileName(fileName);
            parsedFile.setContent(fileContent);
            parsedFile.setOwnerId(user.getUserId());
            this.save(parsedFile);
            return parsedFile.getId();
        }catch (BusinessException e){
            throw e;
        }catch (Exception e){
            throw new BusinessException("文件处理失败：" + fileName + e);
        }
    }
}





