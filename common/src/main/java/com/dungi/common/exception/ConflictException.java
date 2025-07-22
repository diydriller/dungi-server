package com.dungi.common.exception;

import com.dungi.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class ConflictException extends BaseException {
    public ConflictException(BaseResponseStatus status) {
        super(status);
    }
}
