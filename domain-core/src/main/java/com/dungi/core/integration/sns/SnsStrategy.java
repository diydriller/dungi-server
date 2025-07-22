package com.dungi.core.integration.sns;

import com.dungi.common.value.SnsProvider;
import org.springframework.stereotype.Component;

@Component
public interface SnsStrategy {
    String getSnsEmail(String token) throws Exception;

    String getSnsToken(String code) throws Exception;

    SnsProvider getServiceType();
}
