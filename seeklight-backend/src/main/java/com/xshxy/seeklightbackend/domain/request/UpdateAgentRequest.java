package com.xshxy.seeklightbackend.domain.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "UpdateAgentRequest", description = "修改 Agent 请求")
@Data
public class UpdateAgentRequest {

    @Schema(description = "Agent 名称")
    @JsonProperty("agent_name")
    @JsonAlias({"agentName"})
    private String agentName;

    @Schema(description = "Agent 描述")
    @JsonProperty("description")
    private String description;

    @Schema(description = "Agent 类型，例如 fastgpt")
    @JsonProperty("agent_type")
    @JsonAlias({"agentType", "agenttype"})
    private String agentType;

    @Schema(description = "创建人用户 ID")
    @JsonProperty("owner_user_id")
    @JsonAlias({"ownerUserId"})
    private Integer ownerUserId;

    @Schema(description = "所属组织 ID")
    @JsonProperty("owner_group_id")
    @JsonAlias({"ownerGroupId"})
    private Integer ownerGroupId;

    @Schema(description = "第三方应用 ID")
    @JsonProperty("app_id")
    @JsonAlias({"appId"})
    private String appId;

    @Schema(description = "第三方应用 API Key")
    @JsonProperty("api_key")
    @JsonAlias({"apiKey"})
    private String apiKey;

    @Schema(description = "可见性：0-private，1-shared")
    @JsonProperty("visibility")
    private Integer visibility;

    @Schema(description = "状态：0-禁用，1-启用")
    @JsonProperty("status")
    private Integer status;
}
