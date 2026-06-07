package com.jong.msaboard.support.web.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class WebFluxLoggingFilter implements WebFilter {

    private final String urlPattern;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        var targetRequestUrl = exchange.getRequest().getURI().getPath();
        if (!PatternMatchUtils.simpleMatch(urlPattern, targetRequestUrl)) {
            return chain.filter(exchange);
        }

        var startTime = System.currentTimeMillis();

        return chain.filter(exchange).doFinally(signalType -> {

            var executionTime = System.currentTimeMillis() - startTime;

            var requestMethod = exchange.getRequest().getMethod();
            var requestUrl = exchange.getRequest().getURI().getPath();
            var requestQuery = exchange.getRequest().getURI().getQuery();
            var requestHeaders = exchange.getRequest().getHeaders();

            var responseStatus = exchange.getResponse().getStatusCode();
            var responseHeaders = exchange.getResponse().getHeaders();

            if (StringUtils.hasText(requestQuery)) {
                requestUrl += "?" + requestQuery;
            }

            log.info("""
                    API Call Info 
                      - Request URL     : [{}] {}
                      - Request Header  : {}
                      - Response Status : {}
                      - Response Header : {}
                      - Execution Time  : {}ms
                    """,
                requestMethod,
                requestUrl,
                requestHeaders,
                responseStatus,
                responseHeaders,
                executionTime);
        });
    }

}
