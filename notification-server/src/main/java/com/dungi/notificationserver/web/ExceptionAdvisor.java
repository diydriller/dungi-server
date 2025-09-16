package com.dungi.notificationserver.web;

import com.dungi.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

import static com.dungi.common.util.StringUtil.REQUEST_KEY;

@Slf4j
@ControllerAdvice
public class ExceptionAdvisor {
    @ExceptionHandler(BaseException.class)
    public Map<String, Object> handleBaseException(BaseException exception) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, exception);

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("message", exception.getStatus().getMessage());
        return errorResponse;
    }
}
