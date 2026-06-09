package com.jong.msaboard.support.web.factory;

import com.jong.msaboard.support.web.error.ErrorCode;
import com.jong.msaboard.support.web.error.ParamErrorCode;
import com.jong.msaboard.support.web.response.ErrorResponse;
import com.jong.msaboard.support.web.response.ErrorResponse.Detail;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationResult;

public final class ErrorResponseFactory {

    public static ErrorResponse create(String path, ErrorCode errorCode) {
        return ErrorResponse.builder()
            .path(path)
            .status(errorCode.status())
            .code(errorCode.code())
            .message(errorCode.message())
            .build();
    }

    public static ErrorResponse create(String path, BindingResult bindingResult) {
        return create(path, ParamErrorCode.INVALID_PARAMETER)
            .withErrors(bindingResult.getAllErrors().stream()
                .map(error -> Detail.builder()
                    .field(error instanceof FieldError fieldError ? fieldError.getField() : null)
                    .message(error.getDefaultMessage())
                    .build())
                .toList());
    }

    public static ErrorResponse create(String path, MethodValidationResult methodValidationResult) {
        return create(path, ParamErrorCode.INVALID_PARAMETER)
            .withErrors(methodValidationResult.getParameterValidationResults().stream()
                .flatMap(validationResult -> validationResult.getResolvableErrors().stream()
                    .map(error -> Detail.builder()
                        .field(validationResult.getMethodParameter().getParameterName())
                        .message(error.getDefaultMessage())
                        .build()))
                .toList());
    }

}
