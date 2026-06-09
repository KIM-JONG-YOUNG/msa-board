package com.jong.msaboard.support.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
public class WebMvcLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        var startTime = System.currentTimeMillis();

        var requestWrapper = new ContentCachingRequestWrapper(request, 0);
        var responseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(requestWrapper, responseWrapper);

        var executionTime = System.currentTimeMillis() - startTime;

        var requestMethod = requestWrapper.getMethod();
        var requestUrl = requestWrapper.getRequestURI();
        var requestQuery = requestWrapper.getQueryString();
        var requestHeaders = getRequestHeaders(requestWrapper);

        var responseStatus = responseWrapper.getStatus();
        var responseHeaders = getResponseHeaders(responseWrapper);

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

        responseWrapper.copyBodyToResponse();
    }

    private HttpHeaders getRequestHeaders(ContentCachingRequestWrapper request) {
        var headers = new HttpHeaders();
        var headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            var headerName = headerNames.nextElement();
            var headerValues = request.getHeaders(headerName);
            headers.put(headerName, Collections.list(headerValues));
        }
        return headers;
    }

    private HttpHeaders getResponseHeaders(ContentCachingResponseWrapper response) {
        var headers = new HttpHeaders();
        var headerNames = response.getHeaderNames();
        headerNames.stream().forEach(headerName -> {
            var headerValues = response.getHeaders(headerName);
            headers.put(headerName, headerValues.stream().toList());
        });
        return headers;
    }

}
