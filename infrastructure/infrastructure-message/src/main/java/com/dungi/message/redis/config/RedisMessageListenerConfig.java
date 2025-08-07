package com.dungi.message.redis.config;

import com.dungi.message.redis.listener.memo.MemoCreateMessageListener;
import com.dungi.message.redis.listener.memo.MemoDeleteMessageListener;
import com.dungi.message.redis.listener.memo.MemoEditMessageListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import static com.dungi.common.util.StringUtil.*;

@RequiredArgsConstructor
@Configuration
public class RedisMessageListenerConfig {
    private final RedisConnectionFactory redisConnectionFactory;
    private final MemoEditMessageListener memoEditMessageListener;
    private final MemoCreateMessageListener memoCreateMessageListener;
    private final MemoDeleteMessageListener memoDeleteMessageListener;

    @Bean
    public RedisMessageListenerContainer redisMessageListener() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(memoEditMessageListener, new PatternTopic(MEMO_EDIT_CHANNEL));
        container.addMessageListener(memoCreateMessageListener, new PatternTopic(MEMO_CREATE_CHANNEL));
        container.addMessageListener(memoDeleteMessageListener, new PatternTopic(MEMO_DELETE_CHANNEL));
        return container;
    }
}
