package com.xshxy.seeklightbackend.controller;

import com.xshxy.seeklightbackend.common.Result;
import com.xshxy.seeklightbackend.domain.dto.AvailableAgentDto;
import com.xshxy.seeklightbackend.service.TAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "可用智能体")
@RestController
@RequestMapping("/agent")
public class AvailableAgentController {

    @Resource
    private TAgentService agentService;

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/available")
    @Operation(summary = "查询当前登录用户可用的智能体列表", description = "返回当前登录用户自己创建的智能体，以及用户所属组织有权限使用的智能体，并自动去重，不返回敏感信息")
    public Result<List<AvailableAgentDto>> listAvailableAgentsForCurrentUser() {
        return Result.success(agentService.listAvailableAgentsForCurrentUser());
    }
}
