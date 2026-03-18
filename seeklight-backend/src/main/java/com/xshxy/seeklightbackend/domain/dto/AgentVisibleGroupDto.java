package com.xshxy.seeklightbackend.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(name = "AgentVisibleGroupDto", description = "Agent 可见组织信息")
@Data
public class AgentVisibleGroupDto {

    @Schema(description = "组织 ID")
    private Integer groupId;

    @Schema(description = "组织名称")
    private String groupName;

    @Schema(description = "组织描述")
    private String description;

    @Schema(description = "授权人用户 ID")
    private Integer grantedBy;

    @Schema(description = "授权时间")
    private Date createdAt;
}
