package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.dto.AgentListItemDto;
import com.xshxy.seeklightbackend.domain.dto.AgentVisibleGroupDto;
import com.xshxy.seeklightbackend.domain.dto.AvailableAgentDto;
import com.xshxy.seeklightbackend.domain.request.CreateAgentRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateAgentRequest;

import java.util.List;

public interface TAgentService extends IService<TAgent> {

    TAgent createAgent(CreateAgentRequest request);

    TAgent updateAgent(Long agentId, UpdateAgentRequest request);

    Page<AgentListItemDto> pageAgents(Integer current, Integer size, String agentName, String ownerUserName,
                                      String ownerGroupName, Integer status);

    List<AgentVisibleGroupDto> listVisibleGroups(Long agentId);

    List<AvailableAgentDto> listAvailableAgentsForCurrentUser();

    boolean deleteAgent(Long agentId);
}
