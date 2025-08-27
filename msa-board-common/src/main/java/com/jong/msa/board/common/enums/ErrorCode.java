package com.jong.msa.board.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_PARAMETER("파라미터가 유효하지 않습니다."),

    ALREADY_EXISTS_MEMBER_USERNAME("동일한 이름의 회원이 존재합니다."),
    NOT_FOUND_MEMBER("존재하지 않는 회원입니다."),
    NOT_FOUND_MEMBER_USERNAME("존재하지 않는 회원 계정입니다."),
    NOT_MATCHED_MEMBER_PASSWORD("비밀번호가 일치하지 않습니다."),
    NOT_ACTIVE_MEMBER("활성화되지 않은 회원입니다."),

    NOT_FOUND_POST("존재하지 않는 게시글입니다."),
    NOT_POST_WRITER("게시글 작성자가 아닙니다."),
    NOT_FOUND_POST_WRITER("존재하지 않는 게시글 작성자입니다."),
    NOT_ACTIVE_POST("활성화되지 않은 게시글입니다."),

    NOT_FOUND_COMMENT_WRITER("존재하지 않는 댓글 작성자입니다."),
    NOT_FOUND_COMMENT_POST("댓글의 게시글이 존재하지 않습니다."),
    NOT_FOUND_COMMENT_PARENT("존재하지 않는 부모 댓글입니다."),
    NOT_FOUND_COMMENT("존재하지 않는 댓글입니다."),

    NOT_ACCESSIBLE_URL("접근할 수 없는 URL 입니다."),

    EXPIRED_ACCESS_TOKEN("만료된 Access Token 입니다."),
    REVOKED_ACCESS_TOKEN("취소된 Access Token 입니다."),
    INVALID_ACCESS_TOKEN("유효하지 않은 Access Token 입니다."),

    EXPIRED_REFRESH_TOKEN("만료된 Refresh Token 입니다."),
    REVOKED_REFRESH_TOKEN("취소된 Refresh Token 입니다."),
    INVALID_REFRESH_TOKEN("유효하지 않은 Refresh Token 입니다."),

    UNSUPPORTED_REQUEST("지원하지 않는 요청입니다."),
    UNEXPECTED_ERROR("시스템에 오류가 발생했습니다.");

    private final String message;

}
