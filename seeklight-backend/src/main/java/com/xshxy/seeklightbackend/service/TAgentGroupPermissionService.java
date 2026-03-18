package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.request.AddAgentGroupPermissionRequest;

public interface TAgentGroupPermissionService extends IService<TAgentGroupPermission> {

    TAgentGroupPermission addPermission(AddAgentGroupPermissionRequest request);
}
