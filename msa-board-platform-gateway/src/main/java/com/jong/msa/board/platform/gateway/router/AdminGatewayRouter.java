package com.jong.msa.board.platform.gateway.router;

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

@Configuration
@RequiredArgsConstructor
public class AdminGatewayRouter {

    private final TokenAuthFilterFactory tokenAuthFilterFactory;

    @Bean
    RouteLocator joinAdminRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .modifyRequestBody(GatewayFilterUtils.rewrite("group", Group.ADMIN))
                    .setPath("/api/members"))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getMemberRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/members/*").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .rewritePath("/api/admin/members/(?<id>.*)", "/api/members/${id}"))
                .uri(MicroserviceNames.LB_MEMBER_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator writePostRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/posts").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .modifyRequestBody(GatewayFilterUtils.rewriteId("writerId"))
                    .setPath("/apis/posts"))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator modifyPostRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/posts/*").and()
                .method(HttpMethod.PUT).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .rewritePath("/api/admin/posts/(?<id>.*)", "/api/posts/${id}"))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getPostRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/posts/*").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .rewritePath("/api/admin/posts/(?<id>.*)", "/api/posts/${id}"))
                .uri(MicroserviceNames.LB_POST_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator writeCommentRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/comments").and()
                .method(HttpMethod.POST).and()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .modifyRequestBody(GatewayFilterUtils.rewriteId("writerId"))
                    .setPath("/api/comments"))
                .uri(MicroserviceNames.LB_COMMENT_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator getCommentTreeRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/comments/tree").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .setPath("/api/comments/tree"))
                .uri(MicroserviceNames.LB_COMMENT_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchMemberRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/members/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .setPath("/api/admin/members/search"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchPostRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/posts/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .setPath("/api/admin/search/posts"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

    @Bean
    RouteLocator searchCommentRouteLocatorByAdmin(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(predicateSpec -> predicateSpec
                .path("/api/admins/comments/search").and()
                .method(HttpMethod.GET).and()
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .filters(gatewayFilterSpec -> gatewayFilterSpec
                    .filter(tokenAuthFilterFactory.apply(TokenAuthFilterFactory.IS_ADMIN))
                    .setPath("/api/admin/search/comments"))
                .uri(MicroserviceNames.LB_SEARCH_MICROSERVICE))
            .build();
    }

}
