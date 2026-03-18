package com.xshxy.seeklightbackend.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xshxy.seeklightbackend.domain.dto.AgentListItemDto;
import com.xshxy.seeklightbackend.domain.dto.AgentVisibleGroupDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_agent(Agent资源表)】的数据库操作Mapper
* @createDate 2026-03-18 15:08:04
* @Entity com.xshxy.seeklightbackend.domain.TAgent
*/
public interface TAgentMapper extends BaseMapper<TAgent> {

    Page<AgentListItemDto> selectAgentPage(Page<AgentListItemDto> page,
                                           @Param("agentName") String agentName,
                                           @Param("ownerUserName") String ownerUserName,
                                           @Param("ownerGroupName") String ownerGroupName,
                                           @Param("status") Integer status);

    List<AgentVisibleGroupDto> selectVisibleGroups(@Param("agentId") Long agentId);
}




