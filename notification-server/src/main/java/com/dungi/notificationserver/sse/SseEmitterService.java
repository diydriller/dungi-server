package com.dungi.notificationserver.sse;

import com.dungi.common.exception.BaseException;
import com.dungi.common.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.dungi.common.response.BaseResponseStatus.NOT_EXIST_SSE;

@Service
@RequiredArgsConstructor
public class SseEmitterService {
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter createSseEmitter(String key) {
        return sseEmitterRepository.save(key, new SseEmitter(TimeUtil.SSE_DURATION));
    }

    public void deleteSseEmitter(String key) {
        sseEmitterRepository.deleteByKey(key);
    }

    public SseEmitter findSseEmitter(String key) {
        return sseEmitterRepository.findByKey(key)
                .orElseThrow(() -> new BaseException(NOT_EXIST_SSE));
    }

    public void send(Object data, String key, SseEmitter sseEmitter) {
        try {
            sseEmitter.send(
                    SseEmitter.event()
                            .id(key)
                            .data(data, MediaType.APPLICATION_JSON)
            );
        } catch (Exception ex) {
            sseEmitterRepository.deleteByKey(key);
        }
    }
}
