package com.jong.msaboard.microservice.member.error;

import com.jong.msaboard.support.web.error.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum MemberErrorCode implements ErrorCode {

    NOT_FOUND_MEMBER(404, "MEMBER-001", "존재하지 않는 회원입니다."),
    NOT_ACTIVE_MEMBER(410, "MEMBER-002", "삭제된 회원 계정입니다."),
    NOT_FOUND_MEMBER_USERNAME(400, "MEMBER-003", "존재하지 않는 회원 계정입니다."),
    NOT_ACTIVE_MEMBER_USERNAME(410, "MEMBER-004", "삭제된 회원 계정입니다."),
    NOT_MATCHED_MEMBER_PASSWORD(400, "MEMBER-005", "비밀번호가 일치하지 않습니다."),
    ALREADY_EXISTS_MEMBER_USERNAME(409, "MEMBER-006", "이미 존재하는 회원 계정입니다.");

    private final Integer status;
    private final String code;
    private final String message;

}
