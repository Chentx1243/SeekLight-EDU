package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.constant.SecurityRoles;
import com.xshxy.seeklightbackend.domain.TGroup;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.dto.UserDetailDto;
import com.xshxy.seeklightbackend.domain.dto.UserListItemDto;
import com.xshxy.seeklightbackend.domain.request.CreateUserRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateUserRequest;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TUserMapper;
import com.xshxy.seeklightbackend.service.TGroupService;
import com.xshxy.seeklightbackend.service.TUserService;
import jakarta.annotation.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
* @author 闄堝嚡瀹?
* @description 閽堝琛ㄣ€恡_user(骞冲彴鐢ㄦ埛琛?銆戠殑鏁版嵁搴撴搷浣淪ervice瀹炵幇
* @createDate 2025-08-30 16:41:46
*/
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser>
    implements TUserService{

    @Resource
    private TGroupService groupService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Page<UserListItemDto> pageUsers(Integer current, Integer size) {
        Page<UserListItemDto> page = new Page<>(current, size);
        return baseMapper.selectUserPage(page);
    }

    @Override
    public UserDetailDto createUser(CreateUserRequest request) {
        if (request == null) {
            throw new BusinessException("用户信息不能为空");
        }
        if (!StringUtils.hasText(request.getUserAccount())) {
            throw new BusinessException("用户账号不能为空");
        }
        if (!StringUtils.hasText(request.getPassword())) {
            throw new BusinessException("登录密码不能为空");
        }
        if (!StringUtils.hasText(request.getName())) {
            throw new BusinessException("用户名称不能为空");
        }
        if (request.getGroupId() == null) {
            throw new BusinessException("所属分组不能为空");
        }

        String userAccount = request.getUserAccount().trim();
        if (baseMapper.selectByAccount(userAccount) != null) {
            throw new BusinessException("用户账号已存在");
        }

        TGroup group = groupService.getById(request.getGroupId());
        if (group == null) {
            throw new BusinessException("所属分组不存在");
        }

        Date now = new Date();
        TUser user = new TUser();
        user.setUserAccount(userAccount);
        user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        user.setName(request.getName().trim());
        user.setGroupId(request.getGroupId());
        user.setRole(validateRole(request.getRole()));
        user.setEnabled(normalizeEnabled(request.getEnabled(), 1));
        user.setIsDeleted(0);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        save(user);
        return getUserDetail(user.getUserId());
    }

    @Override
    public UserDetailDto getUserDetail(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        UserDetailDto userDetail = baseMapper.selectUserDetailById(userId);
        if (userDetail == null) {
            throw new BusinessException("用户不存在");
        }
        return userDetail;
    }

    @Override
    public UserDetailDto updateUser(Integer userId, UpdateUserRequest request) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        if (request == null) {
            throw new BusinessException("用户信息不能为空");
        }

        TUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (request.getPassword() != null) {
            if (!StringUtils.hasText(request.getPassword())) {
                throw new BusinessException("登录密码不能为空");
            }
            user.setPassword(passwordEncoder.encode(request.getPassword().trim()));
        }
        if (request.getName() != null) {
            if (!StringUtils.hasText(request.getName())) {
                throw new BusinessException("用户名称不能为空");
            }
            user.setName(request.getName().trim());
        }
        if (request.getGroupId() != null) {
            TGroup group = groupService.getById(request.getGroupId());
            if (group == null) {
                throw new BusinessException("所属分组不存在");
            }
            user.setGroupId(request.getGroupId());
        }
        if (request.getRole() != null) {
            user.setRole(validateRole(request.getRole()));
        }
        if (request.getEnabled() != null) {
            user.setEnabled(normalizeEnabled(request.getEnabled(), user.getEnabled()));
        }

        user.setUpdatedAt(new Date());
        updateById(user);
        return getUserDetail(userId);
    }

    @Override
    public boolean deleteUser(Integer userId) {
        if (userId == null) {
            throw new BusinessException("用户ID不能为空");
        }
        TUser user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return removeById(userId);
    }

    private String validateRole(String role) {
        String normalizedRole = SecurityRoles.normalize(role);
        if (!SecurityRoles.ADMIN.equals(normalizedRole) && !SecurityRoles.USER.equals(normalizedRole)) {
            throw new BusinessException("系统角色不合法");
        }
        return normalizedRole;
    }

    private int normalizeEnabled(Integer enabled, int defaultValue) {
        if (enabled == null) {
            return defaultValue;
        }
        if (enabled != 0 && enabled != 1) {
            throw new BusinessException("账户启用状态不合法");
        }
        return enabled;
    }
}
