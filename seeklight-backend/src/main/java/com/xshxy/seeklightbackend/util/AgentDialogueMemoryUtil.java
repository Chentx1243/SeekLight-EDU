package com.xshxy.seeklightbackend.util;

public final class AgentDialogueMemoryUtil {

    private static final String PREFIX = "agent_dialogue:";

    private AgentDialogueMemoryUtil() {
    }

    public static String memoryId(Long agentDialogueId) {
        return PREFIX + agentDialogueId;
    }
}
