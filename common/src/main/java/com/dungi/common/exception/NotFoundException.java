package com.dungi.common.exception;

import com.dungi.common.response.BaseResponseStatus;
import lombok.Getter;

@Getter
public class NotFoundException extends BaseException{
    public NotFoundException(BaseResponseStatus status) {
        super(status);
    }
}
