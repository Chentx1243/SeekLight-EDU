package com.xshxy.seeklightbackend.service.impl;

import com.xshxy.seeklightbackend.service.DialogueTitleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
public class DialogueTitleGenerator {

    private static final int MAX_TITLE_LENGTH = 12;

    @Resource
    private DialogueTitleService dialogueTitleService;

    public String generate(String userContent) {
        if (!StringUtils.hasText(userContent)) {
            return "";
        }

        String trimmedContent = userContent.trim();
        try {
            String title = normalizeTitle(dialogueTitleService.summarize(trimmedContent));
            if (StringUtils.hasText(title)) {
                return title;
            }
        } catch (Exception e) {
            log.warn("总结标题失败，使用默认标题", e);
        }
        return fallbackTitle(trimmedContent);
    }

    private String normalizeTitle(String title) {
        if (!StringUtils.hasText(title)) {
            return "";
        }
        String normalized = title
                .replaceAll("[\\r\\n]+", " ")
                .replaceAll("\\s+", "")
                .replaceFirst("^(标题|对话标题)[:：]", "")
                .replaceAll("^[\"'“”‘’《》]+|[\"'“”‘’《》。！？!?,，、；;：:]+$", "")
                .trim();
        if (normalized.length() > MAX_TITLE_LENGTH) {
            return normalized.substring(0, MAX_TITLE_LENGTH);
        }
        return normalized;
    }

    private String fallbackTitle(String userContent) {
        return userContent.substring(0, Math.min(MAX_TITLE_LENGTH, userContent.length()));
    }
}
