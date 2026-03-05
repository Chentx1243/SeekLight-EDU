package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.config.PersistentChatMemoryStore;
import com.xshxy.seeklightbackend.domain.*;
import com.xshxy.seeklightbackend.domain.resp.BaiduSearchResponse;
import com.xshxy.seeklightbackend.domain.resp.netIntentionResult;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TGroupModelPermissionMapper;
import com.xshxy.seeklightbackend.mapper.TGroupProviderCredentialMapper;
import com.xshxy.seeklightbackend.mapper.TModelProviderMapper;
import com.xshxy.seeklightbackend.domain.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.*;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ChatEveServiceImpl implements ChatEveService {

    @Resource
    private TGroupService groupService;

    @Resource
    private TDialogueService dialogueService;

    @Resource
    private TModelService modelService;

    @Resource
    private PersistentChatMemoryStore store;

    @Resource
    private UserInfoService userInfoService;

    @Resource
    private TGroupModelPermissionMapper permissionMapper;

    @Resource
    private TModelProviderMapper providerMapper;

    @Resource
    private TGroupProviderCredentialMapper credentialMapper;

    @Resource
    private IntentService intentService;

    @Resource
    private BaiduSearchService baiduSearchService;

    @Override
    public SseEmitter chat(SseEmitter emitter, ChatEveRequest chatBody) {
        // 获取用户信息
        TUser user = userInfoService.getUser();
        // 获取分组
        TGroup group = groupService.getById(user.getGroupId());
        // 获取请求的模型
        String modelCode = chatBody.getModel();
        TModel modelEntity = modelService.getOne(new LambdaQueryWrapper<TModel>()
                .eq(TModel::getModelKey, modelCode));
        if (modelEntity == null) {
            throw new BusinessException("模型不合法");
        }
        // 校验用户所在分组是否有权使用该模型
        LambdaQueryWrapper<TGroupModelPermission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.eq(TGroupModelPermission::getModelId,modelEntity.getModelId());
        permissionWrapper.eq(TGroupModelPermission::getGroupId,group.getGroupId());
        TGroupModelPermission permission = permissionMapper.selectOne(permissionWrapper);
        if (permission == null) {
            throw new BusinessException("当前用户所属分组没有该模型使用权限");
        }

        // 从凭证表中获取当前用户的凭证key
        LambdaQueryWrapper<TGroupProviderCredential> credentialWrapper = new LambdaQueryWrapper<>();
        credentialWrapper.eq(TGroupProviderCredential::getGroupId,group.getGroupId());
        credentialWrapper.eq(TGroupProviderCredential::getProviderId,modelEntity.getProvider());
        TGroupProviderCredential credential = credentialMapper.selectOne(credentialWrapper);
        if (credential == null) {
            throw new BusinessException("当前用户没有该供应商的凭证，请先添加凭证（apikey未配置）");
        }

        String apiKey = credential.getApiKey();
        // 获取用户的提问
        List<ChatEveRequest.Message> messages = chatBody.getMessages();
        ChatEveRequest.Message message = messages.get(0);
        String userContent = message.getContent();
        // 获取dialogueId
        Long dialogueId = chatBody.getDialogueId();
        // 构建模型组件（真正用于对话的模型核心）
        // 从供应商获取获取baseURL
        // 根据模型信息，获取供应商信息
        LambdaQueryWrapper<TModelProvider> providerWrapper = new LambdaQueryWrapper<>();
        providerWrapper.eq(TModelProvider::getId,modelEntity.getProvider());
        TModelProvider provider = providerMapper.selectOne(providerWrapper);
        if (provider == null) {
            throw new BusinessException("模型配置错误，请检查");
        }

        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(provider.getBaseUrl())
                .apiKey(apiKey)
                .modelName(modelCode)
                .build();


        // 记忆组件
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(store)
                .maxMessages(5)
                .build();

        // 构建Ai服务
        AssistantService aiService = AiServices.builder(AssistantService.class)
                .streamingChatModel(model)
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        // 判断是否第一次发起对话
        TDialogue dialogue = dialogueService.getById(dialogueId);
        if (dialogue == null){
            throw new BusinessException("对话不存在，请先获取新对话id");
        }
        Integer dialogueUserId = dialogue.getUserId();
        if (dialogueUserId == null || !dialogueUserId.equals(user.getUserId())) {
            throw new BusinessException("非法请求，请先获取新对话id");
        }
        if (dialogue.getTitle() == null || dialogue.getTitle().isBlank()) {
            // 最多截取10个用户输入的内容作为title
            dialogue.setTitle(userContent.substring(0, Math.min(10, userContent.length())));
            if (dialogue.getModelId() == null) {
                dialogue.setModelId(modelEntity.getModelId());
            }
            dialogueService.updateById(dialogue);
        }

        // 拼接提示词阶段
        StringBuilder promptBuilder = new StringBuilder();

        // 联网搜索参数
        if (chatBody.getSearch()){
            // 意图识别：是否需要联网，联网所需要搜索的关键词
            netIntentionResult intention = intentService.intention(userContent);
            // 调用联网搜索接口
            BaiduSearchResponse searchResult = baiduSearchService.search(intention.getQuery());
            String searchContent = searchResult.getReferences().toString();
            promptBuilder.append("请使用网络检索到的资料：")
                    .append(searchContent)
                    .append("回答用户问题：")
                    .append(userContent);
            log.info("触发联网检索，检索关键字是：{}，检索到的内是：{}，最终的提示词是：{}", intention.getQuery(), searchResult, promptBuilder);
        }else {
            promptBuilder.append(userContent);
        }

        // 利用Ai服务发起对话请求
        Long memoryId = chatBody.getDialogueId();
        TokenStream tokenRespond = aiService.chat(memoryId,promptBuilder.toString());
        // 注册流式行为
        tokenRespond.onPartialResponse((String partialResponse) -> {
                    try {
                        emitter.send(partialResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse((ChatResponse response) -> {
                    System.out.println("响应完成，完整结果如下：");
                    System.out.println(response);
                    // 结束响应
                    emitter.complete();

                })
                .onError((Throwable error) -> error.printStackTrace())
                .start();
        return emitter;
    }
}
