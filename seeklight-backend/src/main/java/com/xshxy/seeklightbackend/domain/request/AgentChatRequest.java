package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(name = "AgentChatRequest", description = "Agent聊天请求")
public class AgentChatRequest {

    @Schema(description = "平台用户唯一标识", requiredMode = Schema.RequiredMode.REQUIRED)
    private String user;

    @Schema(description = "Agent会话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long agentDialogueId;

    @Schema(description = "聊天消息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Message> messages;

    @Schema(description = "是否流式返回")
    private Boolean stream = true;

    @Schema(description = "是否返回detail")
    private Boolean detail = false;

    @Schema(description = "FastGPT变量参数")
    private Map<String, Object> variables;

    @Data
    @Schema(name = "AgentChatMessage", description = "Agent聊天消息")
    public static class Message {
        @Schema(description = "角色：user/system/assistant")
        private String role;

        @Schema(description = "消息内容")
        private String content;
    }
}
