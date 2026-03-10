package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(name = "ChatEveRequest", description = "聊天请求")
@Data
public class ChatEveRequest {

    @Schema(description = "平台用户唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String user;

    @Schema(description = "对话ID")
    private Long dialogueId;

    @Schema(description = "模型名称")
    private String model;

    @Schema(description = "聊天消息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Message> messages;

    @Schema(description = "最大 tokens")
    private Integer maxTokens;

    @Schema(description = "采样温度")
    private Double temperature;

    @Schema(description = "是否流式返回")
    private Boolean stream;

    @Schema(description = "联网搜索开关")
    private Boolean search = false;

    @Schema(description = "文件问答：文件id")
    private Integer fileId;

    @Schema(name = "ChatEveMessage", description = "聊天消息")
    @Data
    public static class Message {
        @Schema(description = "角色：user/system/assistant")
        private String role;
        @Schema(description = "消息内容")
        private String content;
    }
}
