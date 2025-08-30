package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.service.TUserService;
import com.xshxy.seeklightbackend.mapper.TUserMapper;
import org.springframework.stereotype.Service;

/**
* @author 陈凯宁
* @description 针对表【t_user(平台用户表)】的数据库操作Service实现
* @createDate 2025-08-30 16:41:46
*/
@Service
public class TUserServiceImpl extends ServiceImpl<TUserMapper, TUser>
    implements TUserService{

}




