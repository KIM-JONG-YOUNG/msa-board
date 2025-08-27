package com.jong.msa.board.microservice.comment.exception;

import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import org.springframework.http.HttpStatus;

public class CommentServiceException extends RestServiceException {

    protected CommentServiceException(HttpStatus status, ErrorCode errorCode) {
        super(status, errorCode);
    }

    public static CommentServiceException notFoundComment() {
        return new CommentServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_COMMENT);
    }

    public static CommentServiceException notFoundCommentWriter() {
        return new CommentServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_COMMENT_WRITER);
    }

    public static CommentServiceException notFoundCommentPost() {
        return new CommentServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_COMMENT_POST);
    }

    public static CommentServiceException notFoundCommentParent() {
        return new CommentServiceException(HttpStatus.NOT_FOUND, ErrorCode.NOT_FOUND_COMMENT_PARENT);
    }

}
