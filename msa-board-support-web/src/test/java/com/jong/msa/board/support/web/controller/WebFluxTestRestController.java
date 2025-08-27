package com.jong.msa.board.support.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxTestRestController {

    @GetMapping(value = "/api/get")
    public Mono<ResponseEntity<Void>> get() {
        return Mono.just(ResponseEntity.noContent().build());
    }

    @PostMapping(value = "/api/post",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Mono<ResponseEntity<Void>> post() {
        return Mono.just(ResponseEntity.noContent().build());
    }

}
