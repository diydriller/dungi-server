package com.dungi.notificationserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {
        "com.dungi.notificationserver",
        "com.dungi.message.kafka.listener.notification",
        "com.dungi.core",
        "com.dungi.common",
        "com.dungi.storage"
})
@SpringBootApplication
public class NotificationServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServerApplication.class, args);
    }
}
