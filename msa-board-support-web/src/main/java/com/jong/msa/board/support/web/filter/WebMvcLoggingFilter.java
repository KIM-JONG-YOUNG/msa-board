package com.jong.msa.board.support.web.filter;

import com.jong.msa.board.support.web.utils.LoggingUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain
    ) throws ServletException, IOException {

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

        logging(requestWrapper);
        logging(responseWrapper);

        responseWrapper.copyBodyToResponse();
    }

    private void logging(ContentCachingRequestWrapper requestWrapper) {

        String method = requestWrapper.getMethod();
        String path = requestWrapper.getRequestURI();
        String query = requestWrapper.getQueryString();
        String url = StringUtils.hasText(query) ? path : path + "?" + query;

        HttpHeaders headers = new HttpHeaders();
        requestWrapper.getHeaderNames().asIterator()
            .forEachRemaining(headerName -> requestWrapper.getHeaders(headerName).asIterator()
                .forEachRemaining(headerValue -> headers.add(headerName, headerValue)));

        String contentType = requestWrapper.getContentType();
        byte[] bodyByte = requestWrapper.getContentAsByteArray();
        String body = LoggingUtils.getLoggingBody(contentType, bodyByte);

        log.info("""
            
            ===================================================
            ====== Request ====================================
             - URL      : [{}] {}
             - Headers  : {}
             - Body     : {}
            ===================================================
            """, url, method, headers, body);
    }

    private void logging(ContentCachingResponseWrapper responseWrapper) {

        int statusCode = responseWrapper.getStatus();

        HttpHeaders headers = new HttpHeaders();
        responseWrapper.getHeaderNames()
            .forEach(headerName -> responseWrapper.getHeaders(headerName)
                .forEach(headerValue -> headers.add(headerName, headerValue)));

        String contentType = responseWrapper.getContentType();
        byte[] bodyByte = responseWrapper.getContentAsByteArray();
        String body = LoggingUtils.getLoggingBody(contentType, bodyByte);

        log.info("""
            
            ===================================================
            ====== Response ====================================
             - Status   : {}
             - Headers  : {}
             - Body     : {}
            ===================================================
            """, statusCode, headers, body);
    }

}
