package com.xshxy.seeklightbackend.mapper;

import com.xshxy.seeklightbackend.domain.TUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author 陈凯宁
* @description 针对表【t_user(平台用户表)】的数据库操作Mapper
* @createDate 2025-08-30 16:41:46
* @Entity com.xshxy.seeklightbackend.domain.TUser
*/
public interface TUserMapper extends BaseMapper<TUser> {

    @Select("SELECT * FROM t_user WHERE user_account = #{account}")
    TUser selectByAccount(@Param("account") String account);

}




