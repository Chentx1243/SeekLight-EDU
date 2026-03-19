package com.xshxy.seeklightbackend.service.impl;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface AgentAssistantService {

    TokenStream chat(@MemoryId String memoryId, @UserMessage String message);
}
