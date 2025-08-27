package com.jong.msa.board.support.web.handler;

import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import com.jong.msa.board.support.web.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcErrorHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex, HttpHeaders headers,
        HttpStatusCode status, WebRequest request
    ) {
        HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .path(httpServletRequest.getRequestURI())
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(ErrorCode.INVALID_PARAMETER.getMessage())
                .validErrors(ex.getAllErrors().stream()
                    .map(ErrorResponse.ValidError::of)
                    .toList())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
        ConstraintViolationException ex, HttpServletRequest request
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(ErrorCode.INVALID_PARAMETER.name())
                .message(ErrorCode.INVALID_PARAMETER.getMessage())
                .validErrors(ex.getConstraintViolations().stream()
                    .map(ErrorResponse.ValidError::of)
                    .toList())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(RestServiceException.class)
    public ResponseEntity<ErrorResponse> handleRestServiceException(
        RestServiceException ex, HttpServletRequest request
    ) {
        return ResponseEntity.status(ex.getStatus())
            .body(ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(ex.getErrorCode().name())
                .message(ex.getErrorCode().getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
        Exception ex, HttpServletRequest request
    ) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .path(request.getRequestURI())
                .code(ErrorCode.UNEXPECTED_ERROR.name())
                .message(ErrorCode.UNEXPECTED_ERROR.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception exception, Object body, HttpHeaders headers,
        HttpStatusCode statusCode, WebRequest request
    ) {
        log.error(exception.getMessage(), exception);
        HttpServletRequest httpServletRequest = ((ServletWebRequest) request).getRequest();
        ErrorCode errorCode = statusCode.is4xxClientError()
            ? ErrorCode.UNSUPPORTED_REQUEST
            : ErrorCode.UNEXPECTED_ERROR;
        return ResponseEntity.status(statusCode)
            .body(ErrorResponse.builder()
                .path(httpServletRequest.getRequestURI())
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build());
    }

}
