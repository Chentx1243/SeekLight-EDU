package com.xshxy.seeklightbackend.domain.resp;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

@Data
public class ModelRouteResult {

    @Description("最终建议调用的模型标识 modelKey，必须从候选模型列表中选择一个")
    private String modelKey;

    @Description("问题复杂度判断，只能输出 low、medium、high")
    private String difficulty;

    @Description("选择该模型的简短原因，控制在一句话内")
    private String reason;
}
