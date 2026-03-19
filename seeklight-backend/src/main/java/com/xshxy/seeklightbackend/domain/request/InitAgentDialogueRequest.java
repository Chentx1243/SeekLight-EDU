package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "InitAgentDialogueRequest", description = "初始化Agent会话请求")
public class InitAgentDialogueRequest {

    @Schema(description = "Agent ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long agentId;
}
