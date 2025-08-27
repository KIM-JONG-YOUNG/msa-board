package com.jong.msa.board.support.web.filter;

import com.jong.msa.board.support.web.utils.LoggingUtils;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxLoggingFilter implements WebFilter, Ordered {

    private static final PathPattern PATH_PATTERN = new PathPatternParser().parse("/api/**");

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        if (!PATH_PATTERN.matches(exchange.getRequest().getPath())) {
            return chain.filter(exchange);
        }

        LoggingRequestDecorator requestDecorator = new LoggingRequestDecorator(exchange);
        LoggingResponseDecorator responseDecorator = new LoggingResponseDecorator(exchange);

        return chain.filter(exchange.mutate()
                .request(requestDecorator)
                .response(responseDecorator)
                .build())
            .doFinally(signalType -> {
                requestDecorator.logging();
                responseDecorator.logging();
            });
    }

    public static class LoggingRequestDecorator extends ServerHttpRequestDecorator {

        private final DataBufferFactory bufferFactory;

        private final Map<String, Object> attributes;

        public LoggingRequestDecorator(ServerWebExchange exchange) {
            super(exchange.getRequest());
            this.bufferFactory = exchange.getResponse().bufferFactory();
            this.attributes = exchange.getAttributes();
            this.attributes.put("request-logging-body", LoggingUtils.EMPTY_BODY);
        }

        @Override
        public Flux<DataBuffer> getBody() {

            MediaType contentType = getHeaders().getContentType();
            if (!LoggingUtils.isLoggableContentType(contentType)) {
                this.attributes.put("request-logging-body", LoggingUtils.NON_LOGGABLE_BODY);
                return super.getBody();
            }

            return DataBufferUtils.join(super.getBody())
                .defaultIfEmpty(bufferFactory.wrap(new byte[0]))
                .flatMapMany(dataBuffer -> {

                    int bodyLength = dataBuffer.readableByteCount();
                    byte[] bodyBytes = new byte[bodyLength];

                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer);

                    String body = LoggingUtils.getLoggingBody(contentType, bodyBytes);
                    this.attributes.put("request-logging-body", body);

                    return Flux.just(bufferFactory.wrap(bodyBytes));
                });
        }

        public void logging() {

            String method = getMethod().name();
            String path = getURI().getPath();
            String query = getURI().getQuery();
            String url = query == null ? path : path + "?" + query;
            HttpHeaders headers = getHeaders();
            String body = (String) this.attributes.get("request-logging-body");

            log.info("""
                    
                    ===================================================
                    ====== Request ====================================
                     - URL      : [{}] {}
                     - Headers  : {}
                     - Body     : {}
                    ===================================================
                    """,
                method, url, headers, body);
        }
    }

    public static class LoggingResponseDecorator extends ServerHttpResponseDecorator {

        private final Map<String, Object> attributes;

        public LoggingResponseDecorator(ServerWebExchange exchange) {
            super(exchange.getResponse());
            this.attributes = exchange.getAttributes();
            this.attributes.put("response-logging-body", LoggingUtils.EMPTY_BODY);
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> responseBody) {

            MediaType contentType = getHeaders().getContentType();
            if (!LoggingUtils.isLoggableContentType(contentType)) {
                this.attributes.put("response-logging-body", LoggingUtils.NON_LOGGABLE_BODY);
                return super.writeWith(responseBody);
            }

            return DataBufferUtils.join(responseBody)
                .flatMap(dataBuffer -> {

                    int bodyLength = dataBuffer.readableByteCount();
                    byte[] bodyBytes = new byte[bodyLength];

                    dataBuffer.read(bodyBytes);
                    DataBufferUtils.release(dataBuffer);

                    String body = LoggingUtils.getLoggingBody(contentType, bodyBytes);
                    this.attributes.put("response-logging-body", body);

                    return super.writeWith(Mono.just(bufferFactory().wrap(bodyBytes)));
                });
        }

        public void logging() {

            HttpStatusCode statusCode = getStatusCode();
            HttpHeaders headers = getHeaders();
            String body = (String) this.attributes.get("response-logging-body");

            log.info("""
                    
                    ===================================================
                    ====== Response ====================================
                     - Status   : {}
                     - Headers  : {}
                     - Body     : {}
                    ===================================================
                    """,
                statusCode, headers, body);
        }
    }

}
