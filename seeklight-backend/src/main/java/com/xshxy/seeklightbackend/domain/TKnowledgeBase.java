package com.xshxy.seeklightbackend.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import lombok.Data;

/**
 * 知识库表
 * @TableName t_knowledge_base
 */
@TableName(value ="t_knowledge_base")
@Data
public class TKnowledgeBase {
    /**
     * 知识库id
     */
    @TableId(type = IdType.AUTO)
    private Integer kbId;

    /**
     * 知识库名称
     */
    private String kbName;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 创建者用户id
     */
    private Integer ownerUserId;

    /**
     * 创建者所属分组id
     */
    private Integer ownerGroupId;

    /**
     * 是否共享到分组（0=否，1=是）
     */
    private Integer isGroupShared;

    /**
     * 逻辑删除标记（0=未删除，1=已删除）
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
        TKnowledgeBase other = (TKnowledgeBase) that;
        return (this.getKbId() == null ? other.getKbId() == null : this.getKbId().equals(other.getKbId()))
            && (this.getKbName() == null ? other.getKbName() == null : this.getKbName().equals(other.getKbName()))
            && (this.getDescription() == null ? other.getDescription() == null : this.getDescription().equals(other.getDescription()))
            && (this.getOwnerUserId() == null ? other.getOwnerUserId() == null : this.getOwnerUserId().equals(other.getOwnerUserId()))
            && (this.getOwnerGroupId() == null ? other.getOwnerGroupId() == null : this.getOwnerGroupId().equals(other.getOwnerGroupId()))
            && (this.getIsGroupShared() == null ? other.getIsGroupShared() == null : this.getIsGroupShared().equals(other.getIsGroupShared()))
            && (this.getIsDeleted() == null ? other.getIsDeleted() == null : this.getIsDeleted().equals(other.getIsDeleted()))
            && (this.getCreatedAt() == null ? other.getCreatedAt() == null : this.getCreatedAt().equals(other.getCreatedAt()))
            && (this.getUpdatedAt() == null ? other.getUpdatedAt() == null : this.getUpdatedAt().equals(other.getUpdatedAt()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getKbId() == null) ? 0 : getKbId().hashCode());
        result = prime * result + ((getKbName() == null) ? 0 : getKbName().hashCode());
        result = prime * result + ((getDescription() == null) ? 0 : getDescription().hashCode());
        result = prime * result + ((getOwnerUserId() == null) ? 0 : getOwnerUserId().hashCode());
        result = prime * result + ((getOwnerGroupId() == null) ? 0 : getOwnerGroupId().hashCode());
        result = prime * result + ((getIsGroupShared() == null) ? 0 : getIsGroupShared().hashCode());
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
        sb.append(", kbId=").append(kbId);
        sb.append(", kbName=").append(kbName);
        sb.append(", description=").append(description);
        sb.append(", ownerUserId=").append(ownerUserId);
        sb.append(", ownerGroupId=").append(ownerGroupId);
        sb.append(", isGroupShared=").append(isGroupShared);
        sb.append(", isDeleted=").append(isDeleted);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append("]");
        return sb.toString();
    }
}