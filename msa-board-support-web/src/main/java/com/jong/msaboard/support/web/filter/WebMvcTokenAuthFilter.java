package com.jong.msaboard.support.web.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.factory.ErrorResponseFactory;
import com.jong.msaboard.support.web.service.TokenMvcService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class WebMvcTokenAuthFilter extends OncePerRequestFilter {

    private final TokenMvcService tokenMvcService;

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            var accessToken = request.getHeader(HeaderNames.ACCESS_TOKEN);
            if (StringUtils.hasText(accessToken)) {
                var authentication = tokenMvcService.getAuthenticationFromAccessToken(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (ErrorCodeException e) {
            var path = request.getRequestURI();
            var errorResponse = ErrorResponseFactory.create(path, e.errorCode());
            response.setStatus(errorResponse.status());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
        }

        filterChain.doFilter(request, response);
    }

}
