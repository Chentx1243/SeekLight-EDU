package com.xshxy.seeklightbackend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.dto.UserDetailDto;
import com.xshxy.seeklightbackend.domain.dto.UserListItemDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author 闄堝嚡瀹?
* @description 閽堝琛ㄣ€恡_user(骞冲彴鐢ㄦ埛琛?銆戠殑鏁版嵁搴撴搷浣淢apper
* @createDate 2025-08-30 16:41:46
* @Entity com.xshxy.seeklightbackend.domain.TUser
*/
public interface TUserMapper extends BaseMapper<TUser> {

    @Select("SELECT * FROM t_user WHERE user_account = #{account}")
    TUser selectByAccount(@Param("account") String account);

    Page<UserListItemDto> selectUserPage(Page<UserListItemDto> page);

    UserDetailDto selectUserDetailById(@Param("userId") Integer userId);
}
