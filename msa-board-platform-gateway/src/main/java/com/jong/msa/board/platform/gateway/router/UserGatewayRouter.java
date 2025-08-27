package com.jong.msa.board.platform.gateway.router;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.platform.gateway.filter.TokenAuthFilterFactory;
import com.jong.msa.board.platform.gateway.utils.GatewayFilterUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class UserGatewayRouter {

    private final WebClient.Builder webClientBuilder;

    private final TokenAuthFilterFactory tokenAuthFilterFactory;

    @Bean
    RouteLocator joinUserRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .modifyRequestBody(GatewayFilterUtils.rewrite("group", Group.USER))
                    .setPath("/api/members"))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator writePostRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/posts").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .modifyRequestBody(GatewayFilterUtils.rewriteId("writerId"))
                    .setPath("/apis/posts"))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator modifyPostRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/posts/*").and()
                .method(HttpMethod.PUT).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .rewritePath("/api/admin/posts/(?<id>.*)", "/api/posts/${id}")
                    .modifyRequestBody(GatewayFilterUtils.rewrite((exchange, body, id) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.POST_MICROSERVICE).build()
                        .get().uri(exchange.getRequest().getPath().value())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .bodyToMono(ObjectNode.class)
                        .flatMap(post -> GatewayFilterUtils.checkPostActive(post))
                        .flatMap(post -> GatewayFilterUtils.checkPostWriter(post, id))
                        .then(Mono.just(body))))
                    .removeJsonAttributes("state"))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getPostRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/posts/*").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .rewritePath("/api/admin/posts/(?<id>.*)", "/api/posts/${id}")
                    .modifyRequestBody(GatewayFilterUtils.rewrite((exchange, body, id) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.POST_MICROSERVICE).build()
                        .get().uri(exchange.getRequest().getPath().value())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .bodyToMono(ObjectNode.class)
                        .flatMap(GatewayFilterUtils::checkPostActive)
                        .then(Mono.just(body))))
                    .modifyRequestBody(GatewayFilterUtils.rewrite((exchange, body, id) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.POST_MICROSERVICE).build()
                        .patch().uri(exchange.getRequest().getPath().value() + "/views/increase")
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .toBodilessEntity()
                        .then(Mono.just(body)))))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator writeCommentRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/comment").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .setPath("/api/comments")
                    .modifyRequestBody(GatewayFilterUtils.rewrite((exchange, body) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.POST_MICROSERVICE).build()
                        .get().uri("/api/posts/" + body.get("postId").asText())
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .bodyToMono(ObjectNode.class)
                        .flatMap(GatewayFilterUtils::checkPostActive)
                        .then(Mono.just(body))))
                    .modifyRequestBody(GatewayFilterUtils.rewriteId("writerId")))
                .uri(MicroserviceNames.LB_COMMENT_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getCommentTreeRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/comment/tree").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .setPath("/api/comments/tree")
                    .modifyRequestBody(GatewayFilterUtils.rewrite((exchange, body) -> webClientBuilder
                        .baseUrl("http://" + MicroserviceNames.POST_MICROSERVICE).build()
                        .get().uri("/api/posts/" + exchange.getRequest().getQueryParams().getFirst("postId"))
                        .accept(MediaType.APPLICATION_JSON)
                        .retrieve()
                        .onStatus(status -> status.isError(), error -> error.createException())
                        .bodyToMono(ObjectNode.class)
                        .flatMap(GatewayFilterUtils::checkPostActive)
                        .then(Mono.just(body)))))
                .uri(MicroserviceNames.LB_COMMENT_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchMemberRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/members/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .setPath("/api/admin/search/members")
                    .removeJsonAttributes("state"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchPostRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/posts/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .setPath("/api/admin/search/posts")
                    .removeJsonAttributes("state"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchCommentRouteLocatorByUser(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/users/comments/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_USER))
                    .setPath("/api/admin/search/comments")
                    .removeJsonAttributes("state"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

}
