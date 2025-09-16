package com.dungi.notificationserver.sse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {
    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    SseEmitter save(String key, SseEmitter sseEmitter) {
        emitters.put(key, sseEmitter);
        return sseEmitter;
    }

    Optional<SseEmitter> findByKey(String key) {
        return Optional.of(emitters.get(key));
    }

    void deleteByKey(String key) {
        emitters.remove(key);
    }
}
