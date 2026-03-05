package com.xshxy.seeklightbackend.domain.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 百度搜索响应对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaiduSearchResponse {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 错误码，当发生异常时返回
     */
    private String code;

    /**
     * 错误消息，当发生异常时返回
     */
    private String message;

    /**
     * 模型回答详情列表
     */
    private List<Reference> references;

    /**
     * 引用对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reference {
        /**
         * 网站图标地址
         */
        private String icon;

        /**
         * 引用编号1、2、3
         */
        private Integer id;

        /**
         * 网页标题
         */
        private String title;

        /**
         * 网页地址
         */
        private String url;

        /**
         * 网站锚文本或网站标题
         */
        private String webAnchor;

        /**
         * 站点名称
         */
        private String website;

        /**
         * 网页内容，显示2000字以内的相关信息原文片段
         */
        private String content;

        /**
         * 原文片段相关性评分（仅type值为web、video、image时存在）
         * 取值范围[0,1]，数值越大越相关
         */
        private Float rerankScore;

        /**
         * 网页权威性评分（仅type值为web时存在）
         * 取值范围[0,1]，数值越大越权威
         */
        private Float authorityScore;

        /**
         * 网页日期
         */
        private String date;

        /**
         * 检索资源类型。返回值：
         * web:网页
         * video:视频内容
         * image：图片
         * aladdin：阿拉丁
         */
        private String type;

        /**
         * 图片详情
         */
        private ImageDetail image;

        /**
         * 视频详情
         */
        private VideoDetail video;

        /**
         * 是否为阿拉丁内容
         */
        @JsonProperty("is_aladdin")
        private Boolean isAladdin;

        /**
         * 阿拉丁详细内容
         */
        private Object aladdin;

        /**
         * 网页相关图片
         */
        private WebExtensions webExtensions;
    }

    /**
     * 图片详情对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageDetail {
        /**
         * 图片链接
         */
        private String url;

        /**
         * 图片高度
         */
        private String height;

        /**
         * 图片宽度
         */
        private String width;
    }

    /**
     * 视频详情对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoDetail {
        /**
         * 视频链接
         */
        private String url;

        /**
         * 视频高度
         */
        private String height;

        /**
         * 视频宽度
         */
        private String width;

        /**
         * 视频大小，单位Bytes
         */
        private String size;

        /**
         * 视频长度，单位秒
         */
        private String duration;

        /**
         * 视频封面图
         */
        private String hoverPic;
    }

    /**
     * 网页扩展对象
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WebExtensions {
        /**
         * 网页相关图片列表
         */
        private List<Image> images;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Image {
            /**
             * 图片链接
             */
            private String url;

            /**
             * 图片高度
             */
            private String height;

            /**
             * 图片宽度
             */
            private String width;
        }
    }
}
