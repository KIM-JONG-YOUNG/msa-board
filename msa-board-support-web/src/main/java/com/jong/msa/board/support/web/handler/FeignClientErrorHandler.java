package com.jong.msa.board.support.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.support.web.exception.FeignServiceException;
import com.jong.msa.board.support.web.response.ErrorResponse;
import feign.FeignException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(name = "org.springframework.cloud.openfeign.clientconfig.FeignClientConfigurer")
public class FeignClientErrorHandler implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        FeignException feignException = FeignException.errorStatus(methodKey, response);
        try {
            HttpStatus status = HttpStatus.valueOf(response.status());
            String body = feignException.contentUTF8();
            ErrorResponse errorResponse = objectMapper.readValue(body, ErrorResponse.class);
            return new FeignServiceException(status, errorResponse);
        } catch (Exception e) {
            return feignException;
        }
    }

}
