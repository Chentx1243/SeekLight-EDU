package com.xshxy.seeklightbackend.domain.dto;

import lombok.Data;

@Data
public class MessageDTO {

    /**
     * user / assistant / system /
     */
    private String role;

    /**
     * 文本内容
     */
    private String content;

}