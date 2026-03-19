package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.request.AddAgentGroupPermissionRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TAgentGroupPermissionMapper;
import com.xshxy.seeklightbackend.service.TAgentGroupPermissionService;
import com.xshxy.seeklightbackend.service.TAgentService;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TUserService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TAgentGroupPermissionServiceImpl extends ServiceImpl<TAgentGroupPermissionMapper, TAgentGroupPermission>
        implements TAgentGroupPermissionService {

    @Resource
    private TAgentService agentService;

    @Resource
    private TGroupService groupService;

    @Resource
    private TUserService userService;

    @Resource
    private UserInfoService userInfoService;

    @Override
    public TAgentGroupPermission addPermission(AddAgentGroupPermissionRequest request) {
        if (request == null) {
            throw new BusinessException("Agent权限信息不能为空");
        }

        TUser currentUser = requireCurrentUser();
        if (request.getAgentId() == null) {
            throw new BusinessException("Agent ID不能为空");
        }
        if (request.getGroupId() == null) {
            throw new BusinessException("组织ID不能为空");
        }

        Integer grantedBy = request.getGrantedBy() != null ? request.getGrantedBy() : currentUser.getUserId();

        TAgent agent = agentService.getById(request.getAgentId());
        if (agent == null) {
            throw new BusinessException("Agent不存在");
        }
        if (!Integer.valueOf(1).equals(agent.getVisibility())) {
            throw new BusinessException("仅共享状态的Agent支持配置组织权限");
        }

        TGroup group = groupService.getById(request.getGroupId());
        if (group == null) {
            throw new BusinessException("组织不存在");
        }

        TUser grantedByUser = userService.getById(grantedBy);
        if (grantedByUser == null) {
            throw new BusinessException("授权人不存在");
        }

        ensurePermissionUnique(request.getAgentId(), request.getGroupId());

        Date now = new Date();
        TAgentGroupPermission permission = new TAgentGroupPermission();
        permission.setAgentId(request.getAgentId());
        permission.setGroupId(request.getGroupId());
        permission.setGrantedBy(grantedBy);
        permission.setIsDeleted(0);
        permission.setCreatedAt(now);
        permission.setUpdatedAt(now);
        save(permission);
        return permission;
    }

    private void ensurePermissionUnique(Long agentId, Integer groupId) {
        LambdaQueryWrapper<TAgentGroupPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TAgentGroupPermission::getAgentId, agentId)
                .eq(TAgentGroupPermission::getGroupId, groupId);
        if (getOne(wrapper) != null) {
            throw new BusinessException("该组织已拥有当前Agent的使用权限");
        }
    }

    private TUser requireCurrentUser() {
        TUser currentUser = userInfoService.getUser();
        if (currentUser == null || currentUser.getUserId() == null) {
            throw new BusinessException("用户未登录");
        }
        return currentUser;
    }
}
