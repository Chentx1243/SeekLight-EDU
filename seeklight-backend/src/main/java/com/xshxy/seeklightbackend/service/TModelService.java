package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TModel;
import com.xshxy.seeklightbackend.domain.dto.ModelDto;

import java.util.List;

/**
* @author 陈凯宁
* @description 针对表【t_model(可用模型表)】的数据库操作Service
* @createDate 2025-08-30 16:41:28
*/
public interface TModelService extends IService<TModel> {

    /**
     * 创建模型（包含业务校验）
     * @param request 模型信息
     * @return 创建后的模型
     */
    TModel createModelWithValidation(TModel request);

    /**
     * 按条件查询模型列表(不做权限认证，全都查询出来)
     * @param modelName 模型名称（模糊匹配）
     * @param status 模型状态
     * @param groupId 分组ID
     * @return 模型列表
     */
    List<TModel> listModelsByCondition(String modelName, Integer status, Integer groupId);

    /**
     * 查询当前登录用户可用的模型列表
     * @param modelName 模型名称（模糊匹配）
     * @param status 模型状态
     * @return 模型列表
     */
    List<ModelDto> listAvailableModelsForUser(String modelName, Integer status);

    /**
     * 更新模型（包含业务校验）
     * @param modelId 模型ID
     * @param request 更新的模型信息
     * @return 更新后的模型
     */
    TModel updateModelWithValidation(Integer modelId, TModel request);

    /**
     * 删除模型（包含业务校验）
     * @param modelId 模型ID
     * @return 是否删除成功
     */
    boolean deleteModelWithValidation(Integer modelId);
}
