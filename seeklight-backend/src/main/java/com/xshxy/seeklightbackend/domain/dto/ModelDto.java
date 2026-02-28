package com.xshxy.seeklightbackend.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.util.Date;

@Data
public class ModelDto {
    /**
     * 模型编号
     */
    @TableId(type = IdType.AUTO)
    private Integer modelId;

    /**
     * 模型展示名称
     */
    private String modelName;

    /**
     * 模型简介
     */
    private String description;

    /**
     * 模型提供方
     */
    private String provider;

    /**
     * 模型请求标识（真实调用名）
     */
    private String modelKey;

    /**
     * 是否上架（1=上架，0=下架）
     */
    private Integer status;

    /**
     * 逻辑删除标记
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private Date createdAt;

    /**
     * 更新时间
     */
    private Date updatedAt;
}
