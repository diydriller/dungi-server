package com.dungi.message.kafka.publisher;

import com.dungi.core.integration.message.common.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaMessagePublisher implements MessagePublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(Object message, String topic) {
        kafkaTemplate.send(topic, message);
    }
}
