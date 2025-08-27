package com.jong.msa.board.platform.gateway.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.platform.gateway.exception.GatewayServiceException;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public final class GatewayFilterUtils {

    public static Mono<Void> setResponseStatus(ServerWebExchange exchange, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        return Mono.empty();
    }

    public static Mono<String> getRequestHeader(ServerWebExchange exchange, String headerName) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(headerName));
    }

    public static Mono<Void> setResponseHeader(ServerWebExchange exchange, String headerName, String headerValue) {
        exchange.getResponse().getHeaders().add(headerName, headerValue);
        return Mono.empty();
    }

    public static Consumer<ModifyRequestBodyGatewayFilterFactory.Config> rewrite(RewriteJsonFunction rewriteFn) {
        return config -> config.setInClass(JsonNode.class).setOutClass(JsonNode.class)
            .setRewriteFunction((exchange, body) -> rewriteFn.apply((ServerWebExchange) exchange, (JsonNode) body));
    }

    public static Consumer<ModifyRequestBodyGatewayFilterFactory.Config> rewrite(RewriteJsonWithAuthIdFunction rewriteFn) {
        return config -> config.setInClass(JsonNode.class).setOutClass(JsonNode.class)
            .setRewriteFunction((exchange, body) -> ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .map(authentication -> authentication.getPrincipal())
                .map(principal -> UUID.fromString((String) principal))
                .flatMap(id -> rewriteFn.apply((ServerWebExchange) exchange, (JsonNode) body, id)));
    }

    public static Consumer<ModifyRequestBodyGatewayFilterFactory.Config> rewrite(String path, String value) {
        return rewrite((exchange, body) -> {
            if (body == null) {
                return Mono.empty();
            }
            if (body instanceof ObjectNode objectNode) {
                return Mono.just(objectNode.put(path, value));
            }
            return Mono.just(body);
        });
    }

    public static Consumer<ModifyRequestBodyGatewayFilterFactory.Config> rewrite(String path, Enum<?> value) {
        return rewrite(path, value.name());
    }

    public static Consumer<ModifyRequestBodyGatewayFilterFactory.Config> rewriteId(String path) {
        return rewrite((exchange, body, id) -> {
            if (body == null) {
                return Mono.empty();
            }
            if (body instanceof ObjectNode objectNode) {
                return Mono.just(objectNode.put(path, id.toString()));
            }
            return Mono.just(body);
        });
    }

    public static Mono<JsonNode> checkPostActive(JsonNode post) {
        State state = State.valueOf(post.get("state").asText());
        if (state != State.ACTIVE) {
            return Mono.error(GatewayServiceException.notPostActive());
        }
        return Mono.just(post);
    }

    public static Mono<JsonNode> checkPostWriter(JsonNode post, UUID writerId) {
        UUID postWriterId = UUID.fromString(post.get("writerId").asText());
        if (writerId.equals(postWriterId)) {
            return Mono.error(GatewayServiceException.notPostWriter());
        }
        return Mono.just(post);
    }

    private Mono<String> getAccessToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HeaderNames.ACCESS_TOKEN));
    }

    private Mono<String> getRefreshToken(ServerWebExchange exchange) {
        return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HeaderNames.REFRESH_TOKEN));
    }

    @FunctionalInterface
    public interface RewriteJsonFunction {

        Mono<JsonNode> apply(ServerWebExchange exchange, JsonNode body);
    }

    @FunctionalInterface
    public interface RewriteJsonWithAuthIdFunction {

        Mono<JsonNode> apply(ServerWebExchange exchange, JsonNode body, UUID id);
    }

}
