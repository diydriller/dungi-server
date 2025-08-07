package com.dungi.apiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@ComponentScan(basePackages = {
        "com.dungi.apiserver",
        "com.dungi.storage",
        "com.dungi.message.kafka.listener.summary",
        "com.dungi.message.kafka.publisher",
        "com.dungi.message.kafka.config",
        "com.dungi.message.redis",
        "com.dungi.redis.config",
        "com.dungi.core",
        "com.dungi.file",
        "com.dungi.sms",
        "com.dungi.sns",
        "com.dungi.common"
})
@EnableAsync
@SpringBootApplication
public class ApiServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiServerApplication.class, args);
    }
}
