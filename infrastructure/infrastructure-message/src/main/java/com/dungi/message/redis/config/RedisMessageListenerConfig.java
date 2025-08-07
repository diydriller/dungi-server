package com.dungi.message.redis.config;

import com.dungi.message.redis.listener.memo.MemoEditMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static com.dungi.common.util.StringUtil.MEMO_EDIT_CHANNEL;

@RequiredArgsConstructor
@Configuration
public class RedisMessageListenerConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final MemoEditMessageListener redisSubscriber;

    @Bean
    public RedisMessageListenerContainer redisMessageListener() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisSubscriber, new PatternTopic(MEMO_EDIT_CHANNEL));
        return container;
    }
}
