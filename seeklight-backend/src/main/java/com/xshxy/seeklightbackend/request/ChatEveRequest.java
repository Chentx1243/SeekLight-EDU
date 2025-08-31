package com.xshxy.seeklightbackend.request;

import lombok.Data;

import java.util.List;

/**
 * 用于接收chatEve的请求实体类
 */
@Data
public class ChatEveRequest {

    /**
     * user seeklight平台唯一用户标识字段
     */
    private String user;

    /**
     * 对话id
     */
    private String dialogueId;
    /**
     * 模型名称
     */
    private String model;

    /**
     * 聊天内容
     */
    private List<Message> messages;

    /**
     * 最大tokens
     */
    private Integer maxTokens;

    /**
     * 采样温度
     */
    private Double temperature;

    /**
     * 是否流式返回
     */
    private Boolean stream;

    /**
     * 聊天内容实体类
     */
    @Data
    public static class Message {
        /**
         * 角色 user/system
         */
        private String role;

        /**
         * 内容
         */
        private String content;
    }

}
