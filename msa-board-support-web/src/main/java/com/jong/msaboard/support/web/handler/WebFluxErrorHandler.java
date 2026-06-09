package com.jong.msaboard.support.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.web.condition.ConditionalOnWebFlux;
import com.jong.msaboard.support.web.error.SystemErrorCode;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.factory.ErrorResponseFactory;
import com.jong.msaboard.support.web.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@ConditionalOnWebFlux
@ConditionalOnMissingBean(WebFluxSecurityErrorHandler.class)
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxErrorHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        if (throwable instanceof ErrorCodeException exception) {
            return handleErrorCodeException(exchange, exception);
        } else if (throwable instanceof HandlerMethodValidationException exception) {
            return handleHandlerMethodValidationException(exchange, exception);
        } else if (throwable instanceof WebExchangeBindException exception) {
            return handleWebExchangeBindException(exchange, exception);
        } else if (throwable instanceof ErrorResponseException exception) {
            return handleErrorResponseException(exchange, exception);
        }
        return handleInternalException(exchange, throwable);
    }

    private Mono<Void> handleErrorCodeException(
        ServerWebExchange exchange,
        ErrorCodeException exception
    ) {
        log.warn(exception.getMessage());
        var path = exchange.getRequest().getURI().getPath();
        var errorCode = exception.errorCode();
        return write(exchange, ErrorResponseFactory.create(path, errorCode));
    }

    private Mono<Void> handleHandlerMethodValidationException(
        ServerWebExchange exchange,
        HandlerMethodValidationException exception
    ) {
        var path = exchange.getRequest().getURI().getPath();
        return write(exchange, ErrorResponseFactory.create(path, exception));
    }

    private Mono<Void> handleWebExchangeBindException(
        ServerWebExchange exchange,
        WebExchangeBindException exception
    ) {
        var path = exchange.getRequest().getURI().getPath();
        return write(exchange, ErrorResponseFactory.create(path, exception));
    }

    private Mono<Void> handleErrorResponseException(
        ServerWebExchange exchange,
        ErrorResponseException exception
    ) {
        log.warn(exception.getMessage());
        var path = exchange.getRequest().getURI().getPath();
        var statusCode = exception.getStatusCode();
        var errorCode = SystemErrorCode.from(statusCode, exception);
        return write(exchange, ErrorResponseFactory.create(path, errorCode));
    }

    private Mono<Void> handleInternalException(
        ServerWebExchange exchange,
        Throwable throwable
    ) {
        log.error(throwable.getMessage(), throwable);
        var path = exchange.getRequest().getURI().getPath();
        var errorCode = SystemErrorCode.from(HttpStatus.INTERNAL_SERVER_ERROR, throwable);
        return write(exchange, ErrorResponseFactory.create(path, errorCode));
    }

    private Mono<Void> write(ServerWebExchange exchange, ErrorResponse errorResponse) {
        var response = exchange.getResponse();
        response.setStatusCode(HttpStatusCode.valueOf(errorResponse.status()));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.fromCallable(() -> {
                var bodyBytes = objectMapper.writeValueAsBytes(errorResponse);
                return response.bufferFactory().wrap(bodyBytes);
            })
            .subscribeOn(Schedulers.boundedElastic()));
    }

}
