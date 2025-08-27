package com.jong.msa.board.microservice.search.controller;

import com.jong.msa.board.microservice.search.request.CommentSearchRequest;
import com.jong.msa.board.microservice.search.request.MemberSearchRequest;
import com.jong.msa.board.microservice.search.request.PostSearchRequest;
import com.jong.msa.board.microservice.search.response.CommentListResponse;
import com.jong.msa.board.microservice.search.response.MemberListResponse;
import com.jong.msa.board.microservice.search.response.PostListResponse;
import com.jong.msa.board.microservice.search.service.SearchCoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class SearchRestController {

    private final SearchCoreService searchCoreService;

    @PostMapping(
        value = "/api/search/members",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberListResponse> search(
        @RequestBody @Valid MemberSearchRequest request
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchCoreService.search(request));
    }

    @PostMapping(
        value = "/api/search/posts",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PostListResponse> search(
        @RequestBody @Valid PostSearchRequest request
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchCoreService.search(request));
    }

    @PostMapping(
        value = "/api/search/comments",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CommentListResponse> search(
        @RequestBody @Valid CommentSearchRequest request
    ) throws Exception {
        return ResponseEntity.status(HttpStatus.OK)
            .body(searchCoreService.search(request));
    }

}
