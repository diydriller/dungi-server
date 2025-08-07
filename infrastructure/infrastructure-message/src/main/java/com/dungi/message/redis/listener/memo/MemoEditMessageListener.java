package com.dungi.message.redis.listener.memo;

import com.dungi.common.event.MemoEditEvent;
import com.dungi.common.exception.ServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class MemoEditMessageListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody(), StandardCharsets.UTF_8);
            Object[] array = objectMapper.readValue(json, Object[].class);
            var memoMessage = objectMapper.convertValue(array[1], MemoEditEvent.class);
            eventPublisher.publishEvent(memoMessage);
        } catch (Exception e) {
            throw new ServerErrorException("failed to deserialize MemoEditEvent from Redis Pub/Sub");
        }
    }
}
