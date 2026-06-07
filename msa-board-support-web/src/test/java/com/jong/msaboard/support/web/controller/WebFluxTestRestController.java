package com.jong.msaboard.support.web.controller;

import com.jong.msaboard.support.web.condition.ConditionalOnWebFlux;
import com.jong.msaboard.support.web.request.ValidateRequest;
import com.jong.msaboard.support.web.validation.NullableNotBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@ConditionalOnWebFlux
@RequestMapping(value = "/api/flux")
public class WebFluxTestRestController {

    @GetMapping(
        value = "/validate/path/{pathVariable}"
    )
    public Mono<ResponseEntity<Void>> validatePath(
        @Min(value = 1, message = "값이 1보다 작을 수 없습니다.")
        @PathVariable Integer pathVariable
    ) {
        log.info("Path: {}", pathVariable);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @GetMapping(
        value = "/validate/query"
    )
    public Mono<ResponseEntity<Void>> validateQuery(
        @NullableNotBlank(message = "값이 비어있을 수 없습니다.")
        String query
    ) {
        log.info("Query: {}", query);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @GetMapping(
        value = "/validate/query/dto"
    )
    public Mono<ResponseEntity<Void>> validateQueryDto(
        @Valid ValidateRequest request
    ) {
        log.info("DTO: {}", request);
        return Mono.just(ResponseEntity.noContent().build());
    }

    @PostMapping(
        value = "/validate/body",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Void>> validateBody(
        @RequestBody @Valid ValidateRequest request
    ) {
        log.info("Body: {}", request);
        return Mono.just(ResponseEntity.noContent().build());
    }

}
