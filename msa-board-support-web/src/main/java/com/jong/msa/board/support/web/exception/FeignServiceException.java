package com.jong.msa.board.support.web.exception;

import com.jong.msa.board.support.web.response.ErrorResponse;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class FeignServiceException extends RuntimeException {

    private final HttpStatus status;

    private final ErrorResponse response;

    public FeignServiceException(HttpStatus status, ErrorResponse response) {
        super("Feign Client 호출에 오류가 발생했습니다.");
        this.status = status;
        this.response = response;
    }

}
