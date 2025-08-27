package com.jong.msa.board.support.web.controller;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcTestRestController {

    @GetMapping(value = "/api/get")
    public ResponseEntity<Void> get() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/api/post",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> post() {
        return ResponseEntity.noContent().build();
    }

}
