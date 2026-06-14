package com.jong.msaboard.support.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.web.condition.ConditionalOnWebMvcSecurity;
import com.jong.msaboard.support.web.error.SecurityErrorCode;
import com.jong.msaboard.support.web.factory.ErrorResponseFactory;
import com.jong.msaboard.support.web.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@ConditionalOnWebMvcSecurity
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebMvcSecurityErrorHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException, ServletException {
        var path = request.getRequestURI();
        var errorCode = SecurityErrorCode.NOT_AUTHORIZED;
        write(response, ErrorResponseFactory.create(path, errorCode));
    }

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        var path = request.getRequestURI();
        var errorCode = SecurityErrorCode.NOT_ACCESSIBLE_URL;
        write(response, ErrorResponseFactory.create(path, errorCode));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDeniedException(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        handle(request, response, accessDeniedException);
    }

    private void write(HttpServletResponse response, ErrorResponse errorResponse) throws IOException {
        response.setStatus(errorResponse.status());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
