package com.jong.msaboard.support.web.exception;

import com.jong.msaboard.support.web.error.ErrorCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
public class ErrorCodeException extends RuntimeException {

    private final ErrorCode errorCode;

    public ErrorCodeException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

}
