package com.jong.msaboard.support.web.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum ParamErrorCode implements ErrorCode {

    INVALID_PARAMETER(400, "PARAM-001", "유효하지 않은 파라미터입니다.");

    private final Integer status;
    private final String code;
    private final String message;

}
