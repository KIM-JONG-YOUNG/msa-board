package com.jong.msa.board.support.web.exception;

import com.jong.msa.board.common.enums.ErrorCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public abstract class RestServiceException extends RuntimeException {

    private final HttpStatus status;

    private final ErrorCode errorCode;

    public RestServiceException(HttpStatus status, ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = status;
        this.errorCode = errorCode;
    }

}
