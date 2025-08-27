package com.jong.msa.board.microservice.comment.controller;

import com.jong.msa.board.microservice.comment.request.CommentCreateRequest;
import com.jong.msa.board.microservice.comment.response.CommentDetailsResponse;
import com.jong.msa.board.microservice.comment.response.CommentTreeResponse;
import com.jong.msa.board.microservice.comment.service.CommentCoreService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentCoreService commentCoreService;

    @PostMapping(
        value = "/api/comments",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(
        @RequestBody CommentCreateRequest request
    ) {
        commentCoreService.create(request);
        String location = "/api/comment/tree?postId=" + request.postId();
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @GetMapping(
        value = "/api/comments/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentDetailsResponse> get(
        @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentCoreService.get(id));
    }

    @GetMapping(
        value = "/api/comments/tree",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentTreeResponse> getTree(
        @RequestParam UUID postId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(commentCoreService.getTree(postId));
    }

}
