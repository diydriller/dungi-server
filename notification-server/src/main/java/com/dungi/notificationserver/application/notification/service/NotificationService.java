package com.dungi.notificationserver.application.notification.service;

import com.dungi.notificationserver.sse.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseEmitterService sseEmitterService;

    public SseEmitter subscribe(String memberId) {
        var sseEmitter = sseEmitterService.createSseEmitter(memberId);
        sseEmitterService.send("subscribe start", memberId, sseEmitter);
        sseEmitter.onTimeout(() -> {
            sseEmitterService.deleteSseEmitter(memberId);
            sseEmitter.complete();
        });
        sseEmitter.onError(error -> {
            sseEmitterService.deleteSseEmitter(memberId);
            sseEmitter.complete();
        });
        sseEmitter.onCompletion(() -> sseEmitterService.deleteSseEmitter(memberId));

        return sseEmitter;
    }
}
