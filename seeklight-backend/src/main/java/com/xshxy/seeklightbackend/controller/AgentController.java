package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.TAgent;
import com.xshxy.seeklightbackend.domain.TAgentGroupPermission;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Agent管理")
@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/agent")
public class AgentController {

    @Resource
    private TAgentService agentService;

    @Resource
    private TAgentGroupPermissionService agentGroupPermissionService;

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
}
