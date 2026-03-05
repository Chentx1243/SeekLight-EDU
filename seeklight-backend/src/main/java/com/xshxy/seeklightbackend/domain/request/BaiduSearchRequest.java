package com.xshxy.seeklightbackend.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 百度搜索请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiduSearchRequest {

    /**
     * 搜索输入； array的长度需要是奇数, role必须是user-assistant-user交替，以user开始以user结束;
     * 在百度搜索时，仅支持单轮输入，若传入多轮输入，则以用户传入最后的content为输入查询。
     */
    private List<Message> messages;

    /**
     * 搜索版本。默认为standard。
     * 可选值：
     * standard：完整版本。
     * lite：标准版本，对召回规模和精排条数简化后的版本，时延表现更好，效果略弱于完整版。
     */
    private String edition;

    /**
     * 使用的搜索引擎版本； 固定值：baidu_search_v2
     */
    private String searchSource;

    /**
     * 支持设置网页、视频、图片、阿拉丁搜索模态
     * 网页top_k最大取值为50，视频top_k最大为10，图片top_k最大为30，阿拉丁top_k最大为5
     * 默认值为： [{"type": "web","top_k": 20},{"type": "video","top_k": 0},{"type": "image","top_k": 0},{"type": "aladdin","top_k": 0}]
     */
    private List<SearchResource> resourceTypeFilter;

    /**
     * 根据SearchFilter下的子条件做检索过滤
     */
    private SearchFilter searchFilter;

    /**
     * 不检索该名单的网页、视频等结果。支持最多20个站点。
     * 示例：["tieba.baidu.com"]
     */
    private List<String> blockWebsites;

    /**
     * 是否开启安全搜索，默认false。
     * 开启后将采用更严格的风控策略，部分可能涉黄、涉恐query将不返回搜索结果。
     */
    private Boolean safeSearch;

    /**
     * 根据网页发布时间进行筛选。
     * 枚举值:
     * week:最近7天
     * month：最近30天
     * semiyear：最近180天
     * year：最近365天
     */
    private String searchRecencyFilter;

    /**
     * 消息对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        /**
         * 角色设定，可选值：
         * user：用户
         * assistant：模型
         */
        private String role;

        /**
         * content为文本时, 对应对话内容，即用户的query问题。
         * 说明：
         * 1.不能为空。
         * 2.多轮对话中，用户最后一次输入content不能为空字符，如空格、"\n"、"\r"、"\f"等。
         */
        private String content;
    }

    /**
     * 搜索资源对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchResource {
        /**
         * 搜索资源类型。可选值：
         * web：网页
         * video：视频
         * image：图片
         * aladdin：阿拉丁
         */
        private String type;

        /**
         * 指定模态最大返回个数。
         */
        private Integer topK;
    }

    /**
     * 搜索过滤对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchFilter {
        /**
         * 条件查询
         */
        private MatchFilter match;

        /**
         * 范围查询
         */
        private RangeFilter range;
    }

    /**
     * 匹配过滤对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchFilter {
        /**
         * 支持设置指定站点的搜索条件，即仅在设置的站点中进行内容搜索，仅对网页、视频结果生效。
         * 目前支持设置100个站点。
         * 示例：["tieba.baidu.com","baike.baidu.com"]
         * 注意：此为付费功能，目前限时免费中
         */
        private List<String> site;
    }

    /**
     * 范围过滤对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RangeFilter {
        /**
         * 发布时间范围过滤
         */
        private PageTimeFilter page_time;
    }

    /**
     * 页面时间过滤对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageTimeFilter {
        /**
         * 时间查询参数，大于或等于
         * 示例："now-1w/d"，2024-07-16前一周、向下做舍入，即大于2024-07-09 00:00:00，包含2024-07-09完整天。
         */
        private String gte;

        /**
         * 时间查询参数，大于
         * 示例："now-1w/d"，2024-07-16前一周、向上做舍入，即大于2024-07-09 23:59:59，不包含2024-07-09完整天。
         */
        private String gt;

        /**
         * 时间查询参数，小于或等于
         * 示例："now-1w/d"，2024-07-16前一周、向上做舍入，即小于2024-07-09 23:59:59，包含2024-07-09完整天。
         */
        private String lte;

        /**
         * 时间查询参数，小于
         * 示例："now-1w/d"，2024-07-16前一周、向下做舍入，即小于2024-07-09 00:00:00，不包含2024-07-09完整天。
         */
        private String lt;
    }
}
