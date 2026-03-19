package com.xshxy.seeklightbackend.domain.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "AddAgentGroupPermissionRequest", description = "新增 Agent 可见组织权限请求")
@Data
public class AddAgentGroupPermissionRequest {

    @Schema(description = "Agent ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("agent_id")
    @JsonAlias({"agentId"})
    private Long agentId;

    @Schema(description = "可见组织 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("group_id")
    @JsonAlias({"groupId"})
    private Integer groupId;

    @Schema(description = "授权人用户 ID，不传时默认取当前登录用户")
    @JsonProperty("granted_by")
    @JsonAlias({"grantedBy"})
    private Integer grantedBy;
}
