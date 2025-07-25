package com.dungi.core.integration.message.common;

public interface MessagePublisher {
    void publish(Object message, String topic);
}
