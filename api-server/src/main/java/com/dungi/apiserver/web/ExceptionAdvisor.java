package com.dungi.apiserver.web;

import com.dungi.common.exception.AuthenticationException;
import com.dungi.common.exception.BaseException;
import com.dungi.common.exception.ConflictException;
import com.dungi.common.exception.NotFoundException;
import com.dungi.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

import static com.dungi.common.response.BaseResponseStatus.SERVER_ERROR;
import static com.dungi.common.util.StringUtil.REQUEST_KEY;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class ExceptionAdvisor {
    private final SimpMessagingTemplate messagingTemplate;

    // 입력값 검증 에러 핸들러
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public BaseResponse<?> handleValidationException(BindException e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, e);
        BindingResult result = e.getBindingResult();
        List<String> errorList = new ArrayList<>();
        result.getFieldErrors().forEach((fieldError) ->
                errorList.add(
                        fieldError.getField() +
                                " : " +
                                fieldError.getDefaultMessage() +
                                " : rejected value is " +
                                fieldError.getRejectedValue()
                )
        );

        return BaseResponse.builder()
                .isSuccess(false)
                .message(String.join(" / ", errorList))
                .code(400)
                .build();
    }

    // 헤더에서 토큰이 없거나 토큰 검증 실패시 에러 핸들러
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public BaseResponse<?> handleAuthenticationException(AuthenticationException e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, e);
        return new BaseResponse<>(e.getStatus());
    }

    // 리소스가 없을 경우 에러 핸들러
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public BaseResponse<?> handleNotFoundException(NotFoundException e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, e);
        return new BaseResponse<>(e.getStatus());
    }

    // 클라이언트와 서버의 상태 충돌이 일어날 경우 에러 핸들러
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ConflictException.class)
    public BaseResponse<?> handleConflictException(ConflictException e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, e);
        return new BaseResponse<>(e.getStatus());
    }

    // 비즈니스 로직 에러 핸들러
    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(BaseException.class)
    public BaseResponse<?> handleBaseException(BaseException e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.info("eventId = {} ", eventId, e);
        return new BaseResponse<>(e.getStatus());
    }

    // 서버 에러 핸들러
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleException(Exception e) {
        String eventId = MDC.get(REQUEST_KEY);
        log.error("eventId = {} ", eventId, e);
        return new BaseResponse<>(SERVER_ERROR);
    }

    @MessageExceptionHandler(BaseException.class)
    public void handleException(BaseException e) {
        var response = new BaseResponse<>(e.getStatus());
        messagingTemplate.convertAndSend("/topic/error", response);
    }
}
