package com.jong.msa.board.support.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.enums.ErrorCode;
import com.jong.msa.board.support.web.exception.RestServiceException;
import com.jong.msa.board.support.web.response.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxErrorHandler implements Ordered, WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        HttpStatusCode statusCode = null;
        ErrorCode errorCode = null;

        List<ErrorResponse.ValidError> validErrors = null;

        if (throwable instanceof RestServiceException ex) {
            statusCode = ex.getStatus();
            errorCode = ex.getErrorCode();
        } else if (throwable instanceof WebExchangeBindException ex) {
            statusCode = HttpStatus.BAD_REQUEST;
            errorCode = ErrorCode.INVALID_PARAMETER;
            validErrors = ex.getAllErrors().stream()
                .map(ErrorResponse.ValidError::of)
                .toList();
        } else if (throwable instanceof ConstraintViolationException ex) {
            statusCode = HttpStatus.BAD_REQUEST;
            errorCode = ErrorCode.INVALID_PARAMETER;
            validErrors = ex.getConstraintViolations().stream()
                .map(ErrorResponse.ValidError::of)
                .toList();
        } else if (throwable instanceof org.springframework.web.ErrorResponse ex) {
            log.error(throwable.getMessage(), throwable);
            statusCode = ex.getStatusCode();
            errorCode = statusCode.is4xxClientError()
                ? ErrorCode.UNSUPPORTED_REQUEST
                : ErrorCode.UNEXPECTED_ERROR;
        } else {
            log.error(throwable.getMessage(), throwable);
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR;
            errorCode = ErrorCode.UNEXPECTED_ERROR;
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .path(request.getURI().getPath())
            .code(errorCode.name())
            .message(errorCode.getMessage())
            .validErrors(validErrors)
            .timestamp(LocalDateTime.now())
            .build();

        response.setStatusCode(statusCode);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.fromCallable(() -> objectMapper
                .writeValueAsBytes(errorResponse))
            .map(bytes -> response.bufferFactory().wrap(bytes))
            .subscribeOn(Schedulers.boundedElastic()));
    }

}
