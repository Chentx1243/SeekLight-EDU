package com.xshxy.seeklightbackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * Agent资源表
 * @TableName t_agent
 */
@TableName(value ="t_agent")
@Data
public class TAgent {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long agentId;

    /**
     * Agent名称
     */
    private String agentName;

    /**
     * Agent简介
     */
    private String description;

    /**
     * Agent来源类型，如 fastgpt
     */
    private String agentType;

    /**
     * 创建人用户id
     */
    private Integer ownerUserId;

    /**
     * 归属分组id
     */
    private Integer ownerGroupId;

    /**
     * 第三方应用id
     */
    private String appId;

    /**
     * 第三方应用秘钥（建议加密存储）
     */
    private String appKey;

    /**
     * 可见性：0 private/ 1 shared
     */
    private Integer visibility;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 逻辑删除标记（0未删除 1已删除）
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
        TAgent other = (TAgent) that;
        return (this.getAgentId() == null ? other.getAgentId() == null : this.getAgentId().equals(other.getAgentId()))
            && (this.getAgentName() == null ? other.getAgentName() == null : this.getAgentName().equals(other.getAgentName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getAgentType() == null ? other.getAgentType() == null : this.getAgentType().equals(other.getAgentType()))
            && (this.getOwnerUserId() == null ? other.getOwnerUserId() == null : this.getOwnerUserId().equals(other.getOwnerUserId()))
            && (this.getOwnerGroupId() == null ? other.getOwnerGroupId() == null : this.getOwnerGroupId().equals(other.getOwnerGroupId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getAppKey() == null ? other.getAppKey() == null : this.getAppKey().equals(other.getAppKey()))
            && (this.getVisibility() == null ? other.getVisibility() == null : this.getVisibility().equals(other.getVisibility()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getAgentId() == null) ? 0 : getAgentId().hashCode());
        result = prime * result + ((getAgentName() == null) ? 0 : getAgentName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getAgentType() == null) ? 0 : getAgentType().hashCode());
        result = prime * result + ((getOwnerUserId() == null) ? 0 : getOwnerUserId().hashCode());
        result = prime * result + ((getOwnerGroupId() == null) ? 0 : getOwnerGroupId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getAppKey() == null) ? 0 : getAppKey().hashCode());
        result = prime * result + ((getVisibility() == null) ? 0 : getVisibility().hashCode());
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
        sb.append(", agentId=").append(agentId);
        sb.append(", agentName=").append(agentName);
        sb.append(", description=").append(description);
        sb.append(", agentType=").append(agentType);
        sb.append(", ownerUserId=").append(ownerUserId);
        sb.append(", ownerGroupId=").append(ownerGroupId);
        sb.append(", appId=").append(appId);
        sb.append(", appKey=").append(appKey);
        sb.append(", visibility=").append(visibility);
        sb.append(", status=").append(status);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}