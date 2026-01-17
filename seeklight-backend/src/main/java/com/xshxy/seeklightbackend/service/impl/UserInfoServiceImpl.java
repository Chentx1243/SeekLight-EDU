package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.service.TUserService;
import com.xshxy.seeklightbackend.service.UserInfoService;
import com.xshxy.seeklightbackend.util.SecurityUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private TUserService userService;

    /**
     * 获取当前登录的用户实体类
     * @return 用户实体类
     */
    @Override
    public TUser getUser() {
        String currentUsername = SecurityUtil.getCurrentUsername();
        LambdaQueryWrapper<TUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TUser::getUserAccount, currentUsername);
        return userService.getOne(wrapper);
    }
}
