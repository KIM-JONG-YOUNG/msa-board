package com.jong.msaboard.support.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.web.condition.ConditionalOnWebFluxSecurity;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.factory.ErrorResponseFactory;
import com.jong.msaboard.support.web.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Component
@ConditionalOnWebFluxSecurity
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebFluxSecurityErrorHandler implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        var path = exchange.getRequest().getURI().getPath();
        var errorCode = SecurityErrorCode.NOT_AUTHORIZED;
        return write(exchange, ErrorResponseFactory.create(path, errorCode));
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException denied) {
        var path = exchange.getRequest().getURI().getPath();
        var errorCode = SecurityErrorCode.NOT_ACCESSIBLE_URL;
        return write(exchange, ErrorResponseFactory.create(path, errorCode));
    }

    protected Mono<Void> write(ServerWebExchange exchange, ErrorResponse errorResponse) {
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
