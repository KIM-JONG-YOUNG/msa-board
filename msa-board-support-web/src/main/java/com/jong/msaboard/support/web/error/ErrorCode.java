package com.jong.msaboard.support.web.error;

import com.jong.msaboard.support.web.exception.ErrorCodeException;

public interface ErrorCode {

    Integer status();

    String code();

    String message();

    default ErrorCodeException toException() {
        return new ErrorCodeException(this);
    }

}
