package com.dungi.common.exception;

import com.dungi.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class AuthenticationException extends BaseException{
    public AuthenticationException(BaseResponseStatus status) {
        super(status);
    }
}
