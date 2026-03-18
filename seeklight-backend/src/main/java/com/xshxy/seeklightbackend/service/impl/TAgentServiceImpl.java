package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.dto.AgentListItemDto;
import com.xshxy.seeklightbackend.domain.dto.AgentVisibleGroupDto;
import com.xshxy.seeklightbackend.domain.request.CreateAgentRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateAgentRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TAgentGroupPermissionMapper;
import com.xshxy.seeklightbackend.mapper.TAgentMapper;
import com.xshxy.seeklightbackend.service.TAgentService;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class TAgentServiceImpl extends ServiceImpl<TAgentMapper, TAgent> implements TAgentService {

    @Resource
    private TUserService userService;

    @Resource
    private TGroupService groupService;

    @Resource
    private TAgentGroupPermissionMapper agentGroupPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TAgent createAgent(CreateAgentRequest request) {
        if (request == null) {
            throw new BusinessException("Agent信息不能为空");
        }

        validateRequiredFields(
                request.getAgentName(),
                request.getAgentType(),
                request.getOwnerUserId(),
                request.getOwnerGroupId(),
                request.getAppId(),
                request.getApiKey(),
                request.getVisibility(),
                request.getStatus()
        );
        validateAssociations(request.getOwnerUserId(), request.getOwnerGroupId());
        validateEnums(request.getVisibility(), request.getStatus());

        Date now = new Date();
        TAgent agent = new TAgent();
        agent.setAgentName(request.getAgentName().trim());
        agent.setDescription(normalizeNullableText(request.getDescription()));
        agent.setAgentType(request.getAgentType().trim());
        agent.setOwnerUserId(request.getOwnerUserId());
        agent.setOwnerGroupId(request.getOwnerGroupId());
        agent.setAppId(request.getAppId().trim());
        agent.setAppKey(request.getApiKey().trim());
        agent.setVisibility(request.getVisibility());
        agent.setStatus(request.getStatus());
        agent.setIsDeleted(0);
        agent.setCreatedAt(now);
        agent.setUpdatedAt(now);
        save(agent);

        // 同步分组的权限
        syncOwnerGroupPermission(agent, request.getOwnerUserId());
        return agent;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TAgent updateAgent(Long agentId, UpdateAgentRequest request) {
        if (agentId == null) {
            throw new BusinessException("Agent ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("Agent信息不能为空");
        }

        TAgent agent = getById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在");
        }

        Integer ownerUserId = request.getOwnerUserId() != null ? request.getOwnerUserId() : agent.getOwnerUserId();
        Integer ownerGroupId = request.getOwnerGroupId() != null ? request.getOwnerGroupId() : agent.getOwnerGroupId();

        if (request.getAgentName() != null) {
            requireText(request.getAgentName(), "Agent名称不能为空");
            agent.setAgentName(request.getAgentName().trim());
        }
        if (request.getDescription() != null) {
            agent.setDescription(normalizeNullableText(request.getDescription()));
        }
        if (request.getAgentType() != null) {
            requireText(request.getAgentType(), "Agent类型不能为空");
            agent.setAgentType(request.getAgentType().trim());
        }
        if (request.getAppId() != null) {
            requireText(request.getAppId(), "应用ID不能为空");
            agent.setAppId(request.getAppId().trim());
        }
        if (request.getApiKey() != null) {
            requireText(request.getApiKey(), "API Key不能为空");
            agent.setAppKey(request.getApiKey().trim());
        }
        if (request.getOwnerUserId() != null || request.getOwnerGroupId() != null) {
            validateAssociations(ownerUserId, ownerGroupId);
            agent.setOwnerUserId(ownerUserId);
            agent.setOwnerGroupId(ownerGroupId);
        }
        if (request.getVisibility() != null) {
            validateVisibility(request.getVisibility());
            agent.setVisibility(request.getVisibility());
        }
        if (request.getStatus() != null) {
            validateStatus(request.getStatus());
            agent.setStatus(request.getStatus());
        }

        agent.setUpdatedAt(new Date());
        updateById(agent);

        // After updates, keep group permissions aligned with the latest visibility and owner group.
        syncPermissionsAfterUpdate(agent, ownerUserId);
        return agent;
    }

    @Override
    public Page<AgentListItemDto> pageAgents(Integer current, Integer size, String agentName, String ownerUserName,
                                             String ownerGroupName, Integer status) {
        if (current == null || current < 1) {
            throw new BusinessException("页码必须大于0");
        }
        if (size == null || size < 1) {
            throw new BusinessException("每页条数必须大于0");
        }
        if (status != null) {
            validateStatus(status);
        }
        Page<AgentListItemDto> page = new Page<>(current, size);
        return baseMapper.selectAgentPage(
                page,
                normalizeNullableText(agentName),
                normalizeNullableText(ownerUserName),
                normalizeNullableText(ownerGroupName),
                status
        );
    }

    @Override
    public List<AgentVisibleGroupDto> listVisibleGroups(Long agentId) {
        if (agentId == null) {
            throw new BusinessException("Agent ID不能为空");
        }
        TAgent agent = getById(agentId);
        if (agent == null) {
            throw new BusinessException("Agent不存在");
        }
        if (!Integer.valueOf(1).equals(agent.getVisibility())) {
            throw new BusinessException("当前Agent不是共享智能体");
        }
        return baseMapper.selectVisibleGroups(agentId);
    }

    private void validateRequiredFields(String agentName,
                                        String agentType,
                                        Integer ownerUserId,
                                        Integer ownerGroupId,
                                        String appId,
                                        String apiKey,
                                        Integer visibility,
                                        Integer status) {
        requireText(agentName, "Agent名称不能为空");
        requireText(agentType, "Agent类型不能为空");
        if (ownerUserId == null) {
            throw new BusinessException("创建人用户ID不能为空");
        }
        if (ownerGroupId == null) {
            throw new BusinessException("所属组织ID不能为空");
        }
        requireText(appId, "应用ID不能为空");
        requireText(apiKey, "API Key不能为空");
        if (visibility == null) {
            throw new BusinessException("可见性不能为空");
        }
        if (status == null) {
            throw new BusinessException("状态不能为空");
        }
    }

    private void validateAssociations(Integer ownerUserId, Integer ownerGroupId) {
        TUser ownerUser = userService.getById(ownerUserId);
        if (ownerUser == null) {
            throw new BusinessException("创建人不存在");
        }

        TGroup ownerGroup = groupService.getById(ownerGroupId);
        if (ownerGroup == null) {
            throw new BusinessException("所属组织不存在");
        }
        if (!ownerGroupId.equals(ownerUser.getGroupId())) {
            throw new BusinessException("创建人不属于所属组织");
        }
    }

    private void validateEnums(Integer visibility, Integer status) {
        validateVisibility(visibility);
        validateStatus(status);
    }

    private void validateVisibility(Integer visibility) {
        if (!Integer.valueOf(0).equals(visibility) && !Integer.valueOf(1).equals(visibility)) {
            throw new BusinessException("可见性仅支持0或1");
        }
    }

    private void validateStatus(Integer status) {
        if (!Integer.valueOf(0).equals(status) && !Integer.valueOf(1).equals(status)) {
            throw new BusinessException("状态仅支持0或1");
        }
    }

    private void requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(message);
        }
    }

    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void syncOwnerGroupPermission(TAgent agent, Integer grantedBy) {
        if (!Integer.valueOf(1).equals(agent.getVisibility())) {
            return;
        }

        LambdaQueryWrapper<TAgentGroupPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TAgentGroupPermission::getAgentId, agent.getAgentId())
                .eq(TAgentGroupPermission::getGroupId, agent.getOwnerGroupId());
        TAgentGroupPermission permission = agentGroupPermissionMapper.selectOne(wrapper);
        if (permission != null) {
            return;
        }

        Date now = new Date();
        TAgentGroupPermission ownerPermission = new TAgentGroupPermission();
        ownerPermission.setAgentId(agent.getAgentId());
        ownerPermission.setGroupId(agent.getOwnerGroupId());
        ownerPermission.setGrantedBy(grantedBy);
        ownerPermission.setIsDeleted(0);
        ownerPermission.setCreatedAt(now);
        ownerPermission.setUpdatedAt(now);
        agentGroupPermissionMapper.insert(ownerPermission);
    }

    private void syncPermissionsAfterUpdate(TAgent agent, Integer grantedBy) {
        if (Integer.valueOf(1).equals(agent.getVisibility())) {
            syncOwnerGroupPermission(agent, grantedBy);
            return;
        }

        // 变更为私有的时：剔除组织权限记录
        LambdaQueryWrapper<TAgentGroupPermission> removeWrapper = new LambdaQueryWrapper<>();
        removeWrapper.eq(TAgentGroupPermission::getAgentId, agent.getAgentId());
        agentGroupPermissionMapper.delete(removeWrapper);
    }
}
