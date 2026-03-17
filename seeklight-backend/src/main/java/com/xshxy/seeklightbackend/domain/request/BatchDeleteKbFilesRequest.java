package com.xshxy.seeklightbackend.domain.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

@Data
@Schema(name = "BatchDeleteKbFilesRequest", description = "批量删除知识库文件请求")
public class BatchDeleteKbFilesRequest {

    @Schema(description = "待删除的文件 ID 列表")
    private List<Integer> fileIds;
}
