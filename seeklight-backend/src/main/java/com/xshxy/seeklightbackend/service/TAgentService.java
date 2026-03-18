package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.request.CreateAgentRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateAgentRequest;

public interface TAgentService extends IService<TAgent> {

    TAgent createAgent(CreateAgentRequest request);

    TAgent updateAgent(Long agentId, UpdateAgentRequest request);
}
