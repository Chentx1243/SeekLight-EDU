package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "ChatKbRequest", description = "知识库对话请求")
@Data
public class ChatKbRequest {
    @Schema(description = "平台用户唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String user;
    private Long dialogueId;
    private String model;
    private Integer kbId;
    private List<Integer> fileIds; // 可选，只搜某文件
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}