package com.dungi.sms.twilio;

import com.dungi.core.integration.sms.SmsSender;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TwilioSmsSender implements SmsSender {
    @Value("${twilio.accountId}")
    private String twilioAccountSid;
    @Value("${twilio.authToken}")
    private String twilioAuthToken;
    @Value("${twilio.adminPhoneNumber}")
    private String adminPhoneNumber;

    @Override
    public void sendSms(String phoneNumber, String randomNumber) {
        Twilio.init(twilioAccountSid, twilioAuthToken);
        Message.creator(
                        new PhoneNumber(phoneNumber),
                        new PhoneNumber(adminPhoneNumber),
                        "enter the number : " + randomNumber)
                .create();
    }
}
