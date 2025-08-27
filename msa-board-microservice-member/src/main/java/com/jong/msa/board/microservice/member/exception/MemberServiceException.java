package com.jong.msa.board.microservice.member.exception;

import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class MemberServiceException extends RestServiceException {

    protected MemberServiceException(HttpStatus status, ErrorCode errorCode) {
        super(status, errorCode);
    }

    public static MemberServiceException notFoundMember() {
        return new MemberServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_MEMBER);
    }

    public static MemberServiceException notFoundMemberUsername() {
        return new MemberServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_FOUND_MEMBER_USERNAME);
    }

    public static MemberServiceException alreadyExistsMemberUsername() {
        return new MemberServiceException(HttpStatus.CONFLICT, ErrorCode.ALREADY_EXISTS_MEMBER_USERNAME);
    }

    public static MemberServiceException notMatchMemberPassword() {
        return new MemberServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_MATCHED_MEMBER_PASSWORD);
    }

    public static MemberServiceException notActiveMember() {
        return new MemberServiceException(HttpStatus.BAD_REQUEST, ErrorCode.NOT_ACTIVE_MEMBER);
    }

}
