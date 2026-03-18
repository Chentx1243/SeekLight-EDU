package com.xshxy.seeklightbackend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
import com.xshxy.seeklightbackend.domain.dto.AgentListItemDto;
import com.xshxy.seeklightbackend.domain.dto.AgentVisibleGroupDto;
import com.xshxy.seeklightbackend.domain.request.AddAgentGroupPermissionRequest;
import com.xshxy.seeklightbackend.domain.request.CreateAgentRequest;
import com.xshxy.seeklightbackend.domain.request.UpdateAgentRequest;
import com.xshxy.seeklightbackend.service.TAgentGroupPermissionService;
import com.xshxy.seeklightbackend.service.TAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Agent管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private TAgentService agentService;

    @Resource
    private TAgentGroupPermissionService agentGroupPermissionService;

    @GetMapping
    @Operation(summary = "分页查询Agent列表", description = "管理员分页查询 Agent 列表，支持按 Agent 名称、创建者、创建者组织、启用状态筛选")
    public Result<Page<AgentListItemDto>> pageAgents(
            @Parameter(description = "当前页码")
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @Parameter(description = "每页大小")
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @Parameter(description = "Agent 名称")
            @RequestParam(value = "agentName", required = false) String agentName,
            @Parameter(description = "Agent 创建者名称")
            @RequestParam(value = "ownerUserName", required = false) String ownerUserName,
            @Parameter(description = "Agent 创建者组织名称")
            @RequestParam(value = "ownerGroupName", required = false) String ownerGroupName,
            @Parameter(description = "启用状态：0-禁用，1-启用")
            @RequestParam(value = "status", required = false) Integer status) {
        return Result.success(agentService.pageAgents(current, size, agentName, ownerUserName, ownerGroupName, status));
    }

    @PostMapping
    @Operation(summary = "新增Agent", description = "管理员新增 Agent，并在共享状态下自动为所属组织补充默认使用权限")
    public Result<TAgent> createAgent(@RequestBody CreateAgentRequest request) {
        return Result.success(agentService.createAgent(request));
    }

    @PutMapping("/{agentId}")
    @Operation(summary = "修改Agent", description = "管理员修改已有 Agent 信息，并同步共享组织权限")
    public Result<TAgent> updateAgent(
            @Parameter(description = "Agent ID", required = true)
            @PathVariable Long agentId,
            @RequestBody UpdateAgentRequest request) {
        return Result.success(agentService.updateAgent(agentId, request));
    }

    @PostMapping("/permission")
    @Operation(summary = "新增Agent可见组织权限", description = "管理员为共享状态的 Agent 新增可见组织，使 Agent 可在多个组织之间共享")
    public Result<TAgentGroupPermission> addGroupPermission(@RequestBody AddAgentGroupPermissionRequest request) {
        return Result.success(agentGroupPermissionService.addPermission(request));
    }

    @GetMapping("/{agentId}/groups")
    @Operation(summary = "查询Agent可见组织列表", description = "根据 Agent ID 查询共享智能体的可见组织范围，仅返回组织展示信息")
    public Result<List<AgentVisibleGroupDto>> listVisibleGroups(
            @Parameter(description = "Agent ID", required = true)
            @PathVariable Long agentId) {
        return Result.success(agentService.listVisibleGroups(agentId));
    }
}
