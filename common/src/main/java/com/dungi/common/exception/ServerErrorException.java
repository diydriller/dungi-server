package com.dungi.common.exception;

import lombok.Getter;

@Getter
public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }
}
