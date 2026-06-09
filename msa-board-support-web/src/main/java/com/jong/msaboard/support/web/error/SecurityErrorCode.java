package com.jong.msaboard.support.web.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum SecurityErrorCode implements ErrorCode {

    NOT_AUTHORIZED(401, "SECURITY-001", "인증되지 않은 사용자입니다."),
    NOT_ACCESSIBLE_URL(403, "SECURITY-002", "접근할 수 없는 URL 입니다."),

    EXPIRED_ACCESS_TOKEN(401, "SECURITY-001", "만료된 Access Token 입니다."),
    REVOKED_ACCESS_TOKEN(401, "SECURITY-002", "사용할 수 없는 Access Token 입니다."),
    INVALID_ACCESS_TOKEN(401, "SECURITY-003", "유효하지 않은 Access Token 입니다."),

    EXPIRED_REFRESH_TOKEN(401, "SECURITY-004", "만료된 Refresh Token 입니다."),
    REVOKED_REFRESH_TOKEN(401, "SECURITY-005", "사용할 수 없는 Refresh Token 입니다."),
    INVALID_REFRESH_TOKEN(401, "SECURITY-006", "유효하지 않은 Refresh Token 입니다.");

    private final Integer status;
    private final String code;
    private final String message;

}
