package com.xshxy.seeklightbackend.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
// 暂时弃用
public class SseEmitterManager {

    // 存储所有的连接，key 可以是 userId、sessionId
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 创建并保存一个新的 SseEmitter
     */
    public SseEmitter createEmitter(String clientId) {
        // 创建一个Emitter实例；设置有效期是10分钟（600000毫秒）
        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L);
        // 保存到 emitters 中
        emitters.put(clientId, emitter);
        // 注册清理逻辑
        // 并输入日志
        emitter.onCompletion(() -> {
            log.info("连接已关闭：{}", clientId);
            emitters.remove(clientId);
        });
        // 有效期结束后（10分钟后，在上面配置）
        emitter.onTimeout(() -> {
            log.info("连接有效期已结束，自动清除：{}", clientId);
            emitters.remove(clientId);
        });
        emitter.onError((e) -> {
            log.info("连接已出错：{}", clientId);
            emitters.remove(clientId);
        });

        return emitter;
    }

    /**
     * 根据 clientId 获取连接
     * 若没有emitter则创建一个并返回
     */
    public SseEmitter getEmitter(String clientId) {
        SseEmitter emitter = emitters.get(clientId);
        // 没有获取到emitter
        if (emitter == null){
            // 创建一个emitter
            emitter = createEmitter(clientId);
        }
        return emitter;
    }
}
