package com.xshxy.seeklightbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xshxy.seeklightbackend.config.PersistentChatMemoryStore;
import com.xshxy.seeklightbackend.domain.*;
import com.xshxy.seeklightbackend.domain.request.ChatKbRequest;
import com.xshxy.seeklightbackend.domain.resp.BaiduSearchResponse;
import com.xshxy.seeklightbackend.domain.resp.netIntentionResult;
import com.xshxy.seeklightbackend.exception.BusinessException;
import com.xshxy.seeklightbackend.mapper.TFileContentMapper;
import com.xshxy.seeklightbackend.mapper.TGroupModelPermissionMapper;
import com.xshxy.seeklightbackend.mapper.TGroupProviderCredentialMapper;
import com.xshxy.seeklightbackend.mapper.TModelProviderMapper;
import com.xshxy.seeklightbackend.domain.request.ChatEveRequest;
import com.xshxy.seeklightbackend.service.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.invocation.InvocationParameters;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;

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

    @Resource
    private TFileContentMapper fileContentMapper;

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private EmbeddingStore<TextSegment> embeddingStore;


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
        StringBuilder systemPromptBuilder = new StringBuilder();

        if (chatBody.getSearch()){
            // 联网搜索参数
            // 意图识别：是否需要联网，联网所需要搜索的关键词
            netIntentionResult intention = intentService.intention(userContent);
            // 调用联网搜索接口
            BaiduSearchResponse searchResult = baiduSearchService.search(intention.getQuery());
            String searchContent = searchResult.getReferences().toString();
            systemPromptBuilder.append("# 你是用户的工程师助理，需要帮助用户完成工作与学习，下面是一些可参考信息的补充")
                    .append("## 调用联网检索工具查询到的信息：")
                    .append(searchContent);
//            log.info("触发联网检索，检索关键字是：{}，检索到的内是：{}，最终的提示词是：{}", intention.getQuery(), searchResult, systemPromptBuilder);
        }else if (chatBody.getFileId() != null ){
            // 文件问答参数
            // 尝试从数据库中根据文件id获取文件内容
            LambdaQueryWrapper<TFileContent> contentWrapper = new LambdaQueryWrapper<>();
            contentWrapper.eq(TFileContent::getId, chatBody.getFileId()).eq(TFileContent::getOwnerId,user.getUserId());
            TFileContent chatFile = fileContentMapper.selectOne(contentWrapper);
            if (chatFile == null || chatFile.getContent().isBlank()){
                throw new BusinessException("文件不存在或当前用户没有文件权限");
            }
            // 文件内容加入到系统提示词中
            systemPromptBuilder.append("## 调用文件解析工具获得的内容：").append(chatFile.getContent());
            // 将当前对话与参考文件绑定
            dialogue.setFileId(chatFile.getId());
            dialogueService.updateById( dialogue);
        }
        else {
            systemPromptBuilder.append("你是一个工程师助理，需要帮助用户完成工作与学习；");
        }

        // 利用Ai服务发起对话请求
        OpenAiStreamingChatModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(provider.getBaseUrl())
                .apiKey(apiKey)
                .modelName(modelCode)
                .temperature(0.89)
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
                .systemMessage(systemPromptBuilder.toString())
                .build();

        Long memoryId = chatBody.getDialogueId();
        TokenStream tokenRespond = aiService.chat(memoryId,userContent);
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

    @Override
    public SseEmitter chatWithKnowledgeBase(SseEmitter emitter, ChatKbRequest chatBody){
        // 获取用户信息
        TUser user = userInfoService.getUser();
        TModel model = checkAndGetModel(chatBody.getModel(), user);
        // 获取模型供应商
        TModelProvider provider = getProvider(model);
        // 获取分组的供应商key
        String apiKey = getApiKey(user, model);
        // 校验对话
        TDialogue dialogue = checkDialogue(chatBody.getDialogueId(), user);

        // 获取用户的提问(并设置对话标题)
        List<ChatKbRequest.Message> messages = chatBody.getMessages();
        ChatKbRequest.Message message = messages.get(0);
        String userContent = message.getContent();
        if (dialogue.getTitle() == null || dialogue.getTitle().isBlank()) {
            // 最多截取10个用户输入的内容作为title
            dialogue.setTitle(userContent.substring(0, Math.min(10, userContent.length())));
            if (dialogue.getModelId() == null) {
                dialogue.setModelId(model.getModelId());
            }
            dialogueService.updateById(dialogue);
        }

        // 构建模型
        OpenAiStreamingChatModel streamingChatModel = OpenAiStreamingChatModel.builder()
                .baseUrl(provider.getBaseUrl())
                .apiKey(apiKey)
                .modelName(model.getModelKey())
                .temperature(0.3)
                .build();
        // query处理器：RAG核心组件
        ContentRetriever contentRetriever = buildKbContentRetriever(embeddingStore, embeddingModel);

        AssistantService assistantService = buildKnowledgeBaseAssistant(streamingChatModel, contentRetriever);
        // 选定特定知识库
        Map<String, Object> queryMeta = new HashMap<>();
        queryMeta.put("kb_id", chatBody.getKbId());
        queryMeta.put("uploader_user_id", user.getUserId());
        if (chatBody.getFileIds() != null && !chatBody.getFileIds().isEmpty()){
            queryMeta.put("file_ids", chatBody.getFileIds());
        }
        InvocationParameters invocationParameters = InvocationParameters.from(queryMeta);

        // 发起知识库问答
        TokenStream tokenStream = assistantService.chatKnowledgeBase(
                chatBody.getDialogueId(),
                userContent,
                invocationParameters
        );

        tokenStream
                .onRetrieved(contents -> {
                    // 这里先可以不处理
                    // 后面你想把命中的知识片段返回前端，再在这里 emitter.send(...)
                    log.info("本次RAG命中片段数量：{}", contents.size());
                })
                .onPartialResponse(partialResponse -> {
                    try {
                        emitter.send(partialResponse);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(response -> {
                    log.info("知识库对话完成，dialogueId={}", chatBody.getDialogueId());
                    emitter.complete();
                })
                .onError(error -> {
                    log.error("知识库对话失败，dialogueId={}", chatBody.getDialogueId(), error);
                    emitter.completeWithError(error);
                })
                .start();

        return emitter;
    }

    /**
     * 工厂方法：构造内容转化器，用于将用户query向量化作RAG搜索
     * @param embeddingStore 向量库
     * @param embeddingModel 向量模型
     * @return ContentRetriever
     */
    private ContentRetriever buildKbContentRetriever(
            EmbeddingStore<TextSegment> embeddingStore,
            EmbeddingModel embeddingModel
    ) {
        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(5)
                .minScore(0.75)
                .dynamicFilter(query -> {
                    Integer kbId = query.metadata().invocationParameters().get("kb_id");
                    Integer userId = query.metadata().invocationParameters().get("uploader_user_id");
                    List<Integer> fileIds = query.metadata().invocationParameters().get("file_ids");

                    Filter filter = metadataKey("kb_id").isEqualTo(kbId)
                            .and(metadataKey("uploader_user_id").isEqualTo(userId));

                    if (fileIds != null) {
                        filter = filter.and(metadataKey("file_id").isIn(fileIds));
                    }

                    return filter;
                })
                .build();
    }

    /**
     * 工厂方法：检索增强器，用于处理RAG的检索到的chunk（重排序与重写query等）
     * @param contentRetriever
     * @return
     */
    private RetrievalAugmentor buildRetrievalAugmentor(ContentRetriever contentRetriever) {
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .build();
    }

    /**
     * 构建知识库问答助手实例
     * @param model
     * @param contentRetriever
     * @return
     */
    private AssistantService buildKnowledgeBaseAssistant(
            OpenAiStreamingChatModel model,
            ContentRetriever contentRetriever
    ) {
        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .chatMemoryStore(store)
                .maxMessages(5)
                .build();

        RetrievalAugmentor retrievalAugmentor = buildRetrievalAugmentor(contentRetriever);

        return AiServices.builder(AssistantService.class)
                // 装载模型
                .streamingChatModel(model)
                // 模型记忆组件
                .chatMemoryProvider(chatMemoryProvider)
                // RAG核心
                .retrievalAugmentor(retrievalAugmentor)
                // RAG片段不记忆
                .storeRetrievedContentInChatMemory(false)
                .build();
    }

    /**
     * 校验模型合法性与用户权限
     * @param modelCode 模型带好
     * @param user 用户对象
     * @return 校验完毕的模型信息
     */
    private TModel checkAndGetModel(String modelCode, TUser user) {
        // 获取分组
        TGroup group = groupService.getById(user.getGroupId());
        // 获取请求的模型
        TModel modelEntity = modelService.getOne(new LambdaQueryWrapper<TModel>()
                .eq(TModel::getModelKey, modelCode));
        if (modelEntity == null) {
            throw new BusinessException("模型不合法");
        }
        // 分组是否用模型使用权
        LambdaQueryWrapper<TGroupModelPermission> permissionWrapper = new LambdaQueryWrapper<>();
        permissionWrapper.eq(TGroupModelPermission::getModelId,modelEntity.getModelId());
        permissionWrapper.eq(TGroupModelPermission::getGroupId,group.getGroupId());
        TGroupModelPermission permission = permissionMapper.selectOne(permissionWrapper);
        if (permission == null) {
            throw new BusinessException("当前用户所属分组没有该模型使用权限");
        }
        return modelEntity;
    }

    /**
     * 尝试获取供应商Key
     * @param user 用户
     * @param modelEntity 模型
     * @return
     */
    private String getApiKey(TUser user, TModel modelEntity) {
        LambdaQueryWrapper<TGroupProviderCredential> credentialWrapper = new LambdaQueryWrapper<>();
        credentialWrapper.eq(TGroupProviderCredential::getGroupId,user.getGroupId());
        credentialWrapper.eq(TGroupProviderCredential::getProviderId,modelEntity.getProvider());
        TGroupProviderCredential credential = credentialMapper.selectOne(credentialWrapper);
        if (credential == null) {
            throw new BusinessException("当前用户没有该供应商的凭证，请先添加凭证（apikey未配置）");
        }
        return credential.getApiKey();
    }

    /**
     * 根据模型获取供应商
     * @param modelEntity
     * @return
     */
    private TModelProvider getProvider(TModel modelEntity) {
        LambdaQueryWrapper<TModelProvider> providerWrapper = new LambdaQueryWrapper<>();
        providerWrapper.eq(TModelProvider::getId,modelEntity.getProvider());
        TModelProvider provider = providerMapper.selectOne(providerWrapper);
        if (provider == null) {
            throw new BusinessException("模型配置错误，请检查");
        }
        return provider;
    }

    /**
     * 对话检查处理：是否存在对话，对话是否属于当前用户
     * @param dialogueId 对话id
     * @param user 用户对象
     * @return
     */
    private TDialogue checkDialogue(Long dialogueId, TUser user) {
        // 判断是否第一次发起对话
        TDialogue dialogue = dialogueService.getById(dialogueId);
        if (dialogue == null){
            throw new BusinessException("对话不存在，请先获取新对话id");
        }
        Integer dialogueUserId = dialogue.getUserId();
        if (dialogueUserId == null || !dialogueUserId.equals(user.getUserId())) {
            throw new BusinessException("非法请求，请先获取新对话id");
        }
        return dialogue;
    }

}
