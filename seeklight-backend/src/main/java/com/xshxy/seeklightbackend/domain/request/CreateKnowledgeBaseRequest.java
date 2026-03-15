package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(name = "CreateKnowledgeBaseRequest", description = "新增知识库请求")
@Data
public class CreateKnowledgeBaseRequest {

    @Schema(description = "知识库名称")
    private String kbName;

    @Schema(description = "知识库描述")
    private String description;

    @Schema(description = "是否共享到分组，0=否，1=是")
    private Integer isGroupShared;
}
