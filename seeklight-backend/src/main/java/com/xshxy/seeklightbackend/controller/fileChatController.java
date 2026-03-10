package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.service.TFileContentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件解析接口")
@RestController
@RequestMapping("/fileChat")
public class fileChatController {

    @Resource
    private TFileContentService fileContentService;

    /**
     * 文件内容解析接口，接受文件，返回信息落库后的fileId
     */
    @PostMapping("/parse")
    public Result<Integer> parseFile(@RequestParam("file") MultipartFile file){
        Integer fileId =  fileContentService.parseFile(file);
        return Result.success(fileId);
    }

}
