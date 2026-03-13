package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TUser;
import com.xshxy.seeklightbackend.domain.dto.UserDetailDto;
import com.xshxy.seeklightbackend.domain.dto.UserListItemDto;
import com.xshxy.seeklightbackend.domain.request.CreateUserRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateUserRequest;

/**
* @author 闄堝嚡瀹?
* @description 閽堝琛ㄣ€恡_user(骞冲彴鐢ㄦ埛琛?銆戠殑鏁版嵁搴撴搷浣淪ervice
* @createDate 2025-08-30 16:41:46
*/
public interface TUserService extends IService<TUser> {

    Page<UserListItemDto> pageUsers(Integer current, Integer size);

    UserDetailDto createUser(CreateUserRequest request);

    UserDetailDto getUserDetail(Integer userId);

    UserDetailDto updateUser(Integer userId, UpdateUserRequest request);

    boolean deleteUser(Integer userId);
}
