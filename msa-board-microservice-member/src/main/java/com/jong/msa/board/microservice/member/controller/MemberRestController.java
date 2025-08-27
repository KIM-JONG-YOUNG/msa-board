package com.jong.msa.board.microservice.member.controller;

import com.jong.msa.board.microservice.member.request.MemberCreateRequest;
import com.jong.msa.board.microservice.member.request.MemberLoginRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyPasswordRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyRequest;
import com.jong.msa.board.microservice.member.response.MemberDetailsResponse;
import com.jong.msa.board.microservice.member.service.MemberCoreService;
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
public class MemberRestController {

    private final MemberCoreService memberCoreService;

    @PostMapping(
        value = "/api/members",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> create(
        @RequestBody @Valid MemberCreateRequest request
    ) {
        UUID id = memberCoreService.create(request);
        String location = "/api/members/" + id;
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @PutMapping(
        value = "/api/members/{id}",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modify(
        @PathVariable UUID id,
        @RequestBody @Valid MemberModifyRequest request
    ) {
        memberCoreService.modify(id, request);
        String location = "/api/members/" + id;
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @PatchMapping(
        value = "/api/members/{id}/password",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> modifyPassword(
        @PathVariable UUID id,
        @RequestBody @Valid MemberModifyPasswordRequest request
    ) {
        memberCoreService.modifyPassword(id, request);
        String location = "/api/members/" + id;
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HttpHeaders.LOCATION, location)
            .build();
    }

    @GetMapping(
        value = "/api/members/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDetailsResponse> get(
        @PathVariable UUID id
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberCoreService.get(id));
    }

    @PostMapping(
        value = "/api/members/login",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDetailsResponse> login(
        @RequestBody @Valid MemberLoginRequest request
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberCoreService.login(request));
    }

}
