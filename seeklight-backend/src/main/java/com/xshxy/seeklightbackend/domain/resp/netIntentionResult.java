package com.xshxy.seeklightbackend.domain.resp;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 联网意图识别结果类（涵提示词）
 */
@Data
public class netIntentionResult {
    @Description("当前用户问题是否需要联网获取时效性信息或者对准确性要求较高的信息")
    private boolean needNet;

    @Description("需要联网检索的关键字，无论是否需要开启联网，query字段确保非空，例如：当用户询问南宁天气时，query输出：南宁 近七日 天气预报 ")
    private String query;

}
