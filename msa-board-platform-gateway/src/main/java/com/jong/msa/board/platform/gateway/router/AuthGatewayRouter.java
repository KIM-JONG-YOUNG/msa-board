package com.jong.msa.board.platform.gateway.router;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jong.msa.board.common.constants.HeaderNames;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.platform.gateway.filter.TokenAuthFilterFactory;
import com.jong.msa.board.platform.gateway.service.TokenManageService;
import com.jong.msa.board.platform.gateway.service.TokenManageService.TokenDetail;
import com.jong.msa.board.platform.gateway.utils.GatewayFilterUtils;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthGatewayRouter {

    private final WebClient.Builder webClientBuilder;

    private final TokenAuthFilterFactory tokenAuthFilterFactory;

    private final TokenManageService tokenManageService;

    @Bean
    RouteLocator loginAuthRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth/login").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter((exchange, chain) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.MEMBER_MICROSERVICE).build()
                        .get().uri("/api/members")
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .bodyToMono(ObjectNode.class)
                        .map(body -> TokenDetail.builder()
                            .id(UUID.fromString(body.get("id").asText()))
                            .group(Group.valueOf(body.get("group").asText()))
                            .build())
                        .flatMap(tokenManageService::generateTokenPair)
                        .flatMap(tokenPair -> Mono.empty()
                            .then(GatewayFilterUtils.setResponseHeader(exchange, HeaderNames.ACCESS_TOKEN, tokenPair.accessToken()))
                            .then(GatewayFilterUtils.setResponseHeader(exchange, HeaderNames.REFRESH_TOKEN, tokenPair.refreshToken())))
                        .then(GatewayFilterUtils.setResponseStatus(exchange, HttpStatus.NO_CONTENT))))
                .uri("no://route"))
            .build();
    }

    @Bean
    RouteLocator logoutAuthRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth/logout").and()
                .method(HttpMethod.POST)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_AUTH))
                    .filter((exchange, chain) -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.ACCESS_TOKEN)
                        .flatMap(tokenManageService::revokeAccessToken)
                        .then(GatewayFilterUtils.setResponseStatus(exchange, HttpStatus.NO_CONTENT))))
                .uri("no://route"))
            .build();
    }

    @Bean
    RouteLocator refreshAuthRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth/refresh").and()
                .method(HttpMethod.POST)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter((exchange, chain) -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.REFRESH_TOKEN)
                        .flatMap(tokenManageService::extractRefreshTokenDetail)
                        .flatMap(tokenManageService::generateTokenPair)
                        .flatMap(tokenPair -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.REFRESH_TOKEN)
                            .flatMap(tokenManageService::revokeRefreshToken)
                            .then(GatewayFilterUtils.setResponseHeader(exchange, HeaderNames.ACCESS_TOKEN, tokenPair.accessToken()))
                            .then(GatewayFilterUtils.setResponseHeader(exchange, HeaderNames.REFRESH_TOKEN, tokenPair.refreshToken())))
                        .then(GatewayFilterUtils.setResponseStatus(exchange, HttpStatus.NO_CONTENT))))
                .uri("no://route"))
            .build();
    }

    @Bean
    RouteLocator modifyAuthRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth").and()
                .method(HttpMethod.PUT).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_AUTH))
                    .filter((exchange, chain) -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.ACCESS_TOKEN)
                        .flatMap(tokenManageService::extractAccessTokenDetail)
                        .flatMap(tokenDetails -> chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                .path("/api/members/" + tokenDetails.id())
                                .build())
                            .build()))))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator modifyAuthPasswordRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth/password").and()
                .method(HttpMethod.PATCH).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_AUTH))
                    .filter((exchange, chain) -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.ACCESS_TOKEN)
                        .flatMap(tokenManageService::extractAccessTokenDetail)
                        .flatMap(tokenDetails -> chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                .path("/api/members/" + tokenDetails.id())
                                .build())
                            .build()))))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getAuthRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/auth").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_AUTH))
                    .filter((exchange, chain) -> GatewayFilterUtils.getRequestHeader(exchange, HeaderNames.ACCESS_TOKEN)
                        .flatMap(tokenManageService::extractAccessTokenDetail)
                        .flatMap(tokenDetails -> chain.filter(exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                .path("/api/members/" + tokenDetails.id())
                                .build())
                            .build()))))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

}
