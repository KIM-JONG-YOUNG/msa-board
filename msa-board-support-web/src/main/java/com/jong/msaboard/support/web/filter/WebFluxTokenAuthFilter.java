package com.jong.msaboard.support.web.filter;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.support.web.service.TokenFluxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class WebFluxTokenAuthFilter implements WebFilter {

    private final TokenFluxService tokenService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var requestHeaders = exchange.getRequest().getHeaders();
        var accessToken = requestHeaders.getFirst(HeaderNames.ACCESS_TOKEN);
        if (!StringUtils.hasText(accessToken)) {
            return chain.filter(exchange);
        }

        return tokenService.getAuthenticationFromAccessToken(accessToken)
            .flatMap(authentication -> chain.filter(exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
    }

}
