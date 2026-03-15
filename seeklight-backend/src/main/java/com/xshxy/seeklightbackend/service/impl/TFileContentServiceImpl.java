package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TFileContent;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TFileContentMapper;
import com.xshxy.seeklightbackend.service.TFileContentService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import com.xshxy.seeklightbackend.util.DocumentParseUtil;
import java.io.InputStream;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class TFileContentServiceImpl extends ServiceImpl<TFileContentMapper, TFileContent>
        implements TFileContentService {

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TFileContentMapper fileContentMapper;

    @Override
    public Integer parseFile(MultipartFile file) {
        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("User is not logged in");
        }

        String fileName = getValidatedFileName(file);
        String fileContent = parseFileContent(file);

        TFileContent parsedFile = new TFileContent();
        parsedFile.setFileName(fileName);
        parsedFile.setContent(fileContent);
        parsedFile.setOwnerId(user.getUserId());
        save(parsedFile);
        return parsedFile.getId();
    }

    @Override
    public String parseFileContent(MultipartFile file) {
        String fileName = getValidatedFileName(file);
        try {
            return parseFileContent(file.getInputStream(), fileName);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to parse file: " + fileName + ", cause: " + e.getMessage());
        }
    }

    @Override
    public String parseFileContent(InputStream inputStream, String fileType) {
        validateInputStream(inputStream);
        String normalizedFileType = normalizeFileType(fileType);

        try {
            if (normalizedFileType.endsWith(".pdf")) {
                return DocumentParseUtil.parsePdf(inputStream);
            }
            if (normalizedFileType.endsWith(".docx")) {
                return DocumentParseUtil.parseWord(inputStream);
            }
            if (normalizedFileType.endsWith(".xlsx")) {
                return DocumentParseUtil.parseExcel(inputStream);
            }
            if (normalizedFileType.endsWith(".txt")) {
                return DocumentParseUtil.parseTxt(inputStream);
            }
            throw new BusinessException("Unsupported file type");
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Failed to parse file: " + fileType + ", cause: " + e.getMessage());
        }
    }

    @Override
    public String getFileNameById(Integer fileId) {
        if (fileId == null) {
            throw new BusinessException("File ID cannot be null");
        }

        TUser user = userInfoService.getUser();
        if (user == null || user.getUserId() == null) {
            throw new BusinessException("User is not logged in");
        }

        String fileName = fileContentMapper.selectFileNameByIdAndOwner(fileId, user.getUserId());
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException("File does not exist or access is denied");
        }

        return fileName;
    }

    private String getValidatedFileName(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File cannot be empty");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new BusinessException("File name cannot be blank");
        }

        return fileName;
    }

    private void validateInputStream(InputStream inputStream) {
        if (inputStream == null) {
            throw new BusinessException("InputStream cannot be null");
        }
    }

    private String normalizeFileType(String fileType) {
        if (fileType == null || fileType.isBlank()) {
            throw new BusinessException("File type cannot be blank");
        }
        return fileType.trim().toLowerCase();
    }
}
