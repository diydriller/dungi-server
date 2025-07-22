package com.dungi.core.integration.sms;

import org.springframework.stereotype.Component;

@Component
public interface SmsSender {
    void sendSms(String phoneNumber, String content);
}
