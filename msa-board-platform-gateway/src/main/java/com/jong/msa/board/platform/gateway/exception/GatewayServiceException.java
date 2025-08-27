package com.jong.msa.board.platform.gateway.exception;

import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import org.springframework.http.HttpStatus;

public class GatewayServiceException extends RestServiceException {

    protected GatewayServiceException(HttpStatus status, ErrorCode errorCode) {
        super(status, errorCode);
    }

    public static GatewayServiceException notAccessibleUrl() {
        return new GatewayServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_ACCESSIBLE_URL);
    }

    public static GatewayServiceException notPostWriter() {
        return new GatewayServiceException(HttpStatus.FORBIDDEN, ErrorCode.NOT_POST_WRITER);
    }

    public static GatewayServiceException notPostActive() {
        return new GatewayServiceException(HttpStatus.GONE, ErrorCode.NOT_ACTIVE_POST);
    }

}
