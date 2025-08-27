package com.jong.msa.board.microservice.post.exception;

import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class PostServiceException extends RestServiceException {

    protected PostServiceException(HttpStatus status, ErrorCode errorCode) {
        super(status, errorCode);
    }

    public static PostServiceException notFoundPostWriter() {
        return new PostServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_POST_WRITER);
    }

    public static PostServiceException notFoundPost() {
        return new PostServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_POST);
    }

}
