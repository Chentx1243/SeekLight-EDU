package com.xshxy.seeklightbackend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xshxy.seeklightbackend.domain.TKnowledgeBase;

public interface TKnowledgeBaseService extends IService<TKnowledgeBase> {

    TKnowledgeBase createKnowledgeBase(TKnowledgeBase request);

    Page<TKnowledgeBase> pageKnowledgeBases(long current, long size, String kbName);

    TKnowledgeBase getKnowledgeBaseDetail(Integer kbId);

    TKnowledgeBase updateKnowledgeBase(Integer kbId, TKnowledgeBase request);

    boolean deleteKnowledgeBase(Integer kbId);
}
