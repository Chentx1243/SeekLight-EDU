package com.xshxy.seeklightbackend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "AvailableAgentDto", description = "当前登录用户可用的智能体信息")
@Data
public class AvailableAgentDto {

    @Schema(description = "Agent ID")
    private Long agentId;

    @Schema(description = "Agent 名称")
    private String agentName;

    @Schema(description = "Agent 描述")
    private String description;

    @Schema(description = "Agent 类型")
    private String agentType;

    @Schema(description = "创建人用户 ID")
    private Integer ownerUserId;

    @Schema(description = "创建人名称")
    private String ownerUserName;

    @Schema(description = "所属组织 ID")
    private Integer ownerGroupId;

    @Schema(description = "所属组织名称")
    private String ownerGroupName;

    @Schema(description = "可见性：0-private，1-shared")
    private Integer visibility;

    @Schema(description = "状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
