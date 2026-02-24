package com.xshxy.seeklightbackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 模型供应商
 * @TableName t_model_provider
 */
@TableName(value ="t_model_provider")
@Data
public class TModelProvider {
    /**
     * 模型供应商实例 id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 所属展示模型 id
     */
    private Integer modelId;

    /**
     * 供应商名称
     */
    private String providerName;

    /**
     * 接口地址
     */
    private String baseUrl;

    /**
     * 供应商预设提示词
     */
    private String promptTemplate;

    /**
     * 是否启用（1=启用，0=禁用）
     */
    private Integer status;

    /**
     * 逻辑删除标志位（0=未删除，1=已删除）
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

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TModelProvider other = (TModelProvider) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getModelId() == null ? other.getModelId() == null : this.getModelId().equals(other.getModelId()))
            && (this.getProviderName() == null ? other.getProviderName() == null : this.getProviderName().equals(other.getProviderName()))
            && (this.getBaseUrl() == null ? other.getBaseUrl() == null : this.getBaseUrl().equals(other.getBaseUrl()))
            && (this.getPromptTemplate() == null ? other.getPromptTemplate() == null : this.getPromptTemplate().equals(other.getPromptTemplate()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getModelId() == null) ? 0 : getModelId().hashCode());
        result = prime * result + ((getProviderName() == null) ? 0 : getProviderName().hashCode());
        result = prime * result + ((getBaseUrl() == null) ? 0 : getBaseUrl().hashCode());
        result = prime * result + ((getPromptTemplate() == null) ? 0 : getPromptTemplate().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getIsDeleted() == null) ? 0 : getIsDeleted().hashCode());
        result = prime * result + ((getCreatedAt() == null) ? 0 : getCreatedAt().hashCode());
        result = prime * result + ((getUpdatedAt() == null) ? 0 : getUpdatedAt().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", modelId=").append(modelId);
        sb.append(", providerName=").append(providerName);
        sb.append(", baseUrl=").append(baseUrl);
        sb.append(", promptTemplate=").append(promptTemplate);
        sb.append(", status=").append(status);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}