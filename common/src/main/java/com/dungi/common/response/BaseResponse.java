package com.dungi.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.dungi.common.response.BaseResponseStatus.SUCCESS;

@Getter
@Builder
@AllArgsConstructor
public class BaseResponse<T> {

    private final Boolean isSuccess;
    private final String message;
    private final int code;
    private T data;

    public BaseResponse(T data) {
        this.isSuccess = SUCCESS.isSuccess();
        this.message = SUCCESS.getMessage();
        this.code = SUCCESS.getCode();
        this.data = data;
    }
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }
}

