package com.jong.msa.board.microservice.post.controller;

import com.jong.msa.board.microservice.post.request.PostCreateRequest;
import com.jong.msa.board.microservice.post.request.PostModifyRequest;
import com.jong.msa.board.microservice.post.response.PostDetailsResponse;
import com.jong.msa.board.microservice.post.service.PostCoreService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class PostRestController {

    private final PostCoreService postCoreService;

    @PostMapping(
        value = "/api/posts",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(
        @RequestBody @Valid PostCreateRequest request
    ) {
        UUID id = postCoreService.create(request);
        String location = "/api/posts/" + id;
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @PutMapping(
        value = "/api/posts/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(
        @PathVariable UUID id,
        @RequestBody @Valid PostModifyRequest request
    ) {
        postCoreService.modify(id, request);
        String location = "/api/posts/" + id;
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @PatchMapping(
        value = "/api/posts/{id}/views/increase")
    public ResponseEntity<Void> increaseViews(
        @PathVariable UUID id
    ) {
        postCoreService.increaseViews(id);
        String location = "/api/posts/" + id;
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @GetMapping(
        value = "/api/posts/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostDetailsResponse> get(
        @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(postCoreService.get(id));
    }

}
