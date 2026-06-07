package com.jong.msaboard.support.web.handler;

import com.jong.msaboard.support.web.condition.ConditionalOnWebMvc;
import com.jong.msaboard.support.web.error.SystemErrorCode;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.factory.ErrorResponseFactory;
import com.jong.msaboard.support.web.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebMvc
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ErrorCodeException.class)
    public ResponseEntity<ErrorResponse> handleErrorCodeException(
        ErrorCodeException exception,
        HttpServletRequest request
    ) {
        log.warn(exception.getMessage());
        var path = request.getRequestURI();
        var errorCode = exception.errorCode();
        var errorResponse = ErrorResponseFactory.createErrorResponse(path, errorCode);
        return ResponseEntity.status(errorResponse.status())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
        HandlerMethodValidationException exception,
        HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest
    ) {
        var request = ((ServletWebRequest) webRequest).getRequest();
        var path = request.getRequestURI();
        var errorResponse = ErrorResponseFactory.createErrorResponse(path, exception);
        return ResponseEntity.status(errorResponse.status())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException exception,
        HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest
    ) {
        var request = ((ServletWebRequest) webRequest).getRequest();
        var path = request.getRequestURI();
        var errorResponse = ErrorResponseFactory.createErrorResponse(path, exception);
        return ResponseEntity.status(errorResponse.status())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        Exception exception, Object body,
        HttpHeaders headers, HttpStatusCode statusCode, WebRequest webRequest
    ) {
        log.warn(exception.getMessage());
        var request = ((ServletWebRequest) webRequest).getRequest();
        var path = request.getRequestURI();
        var errorCode = SystemErrorCode.from(statusCode, exception);
        var errorResponse = ErrorResponseFactory.createErrorResponse(path, errorCode);
        return ResponseEntity.status(errorResponse.status())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOtherException(
        Exception exception,
        HttpServletRequest request
    ) {
        log.error(exception.getMessage(), exception);
        var path = request.getRequestURI();
        var errorCode = SystemErrorCode.from(HttpStatus.INTERNAL_SERVER_ERROR, exception);
        var errorResponse = ErrorResponseFactory.createErrorResponse(path, errorCode);
        return ResponseEntity.status(errorResponse.status())
            .contentType(MediaType.APPLICATION_JSON)
            .body(errorResponse);
    }

}
