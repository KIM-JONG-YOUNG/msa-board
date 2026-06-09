package com.jong.msaboard.support.web.error;

import org.springframework.http.HttpStatusCode;

public record SystemErrorCode(
    Integer status,
    String code,
    String message
) implements ErrorCode {

    public static SystemErrorCode from(HttpStatusCode statusCode, Throwable throwable) {
        var status = statusCode.value();
        var code = statusCode.is4xxClientError() ? "SYSTEM-001" : "SYSTEM-002";
        var message = throwable.getMessage();
        return new SystemErrorCode(status, code, message);
    }

}
