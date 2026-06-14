package com.jong.msaboard.microservice.member.controller;

import com.jong.msaboard.microservice.member.response.internal.MemberDetailInternalResponse;
import com.jong.msaboard.microservice.member.service.MemberInternalService;
import com.jong.msaboard.support.web.constants.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/internal/members")
public class MemberInternalRestController {

    private final MemberInternalService memberInternalService;

    @Operation(summary = "특정 회원 상세 조회")
    @PreAuthorize(SecurityExpressions.IS_ADMIN)
    @GetMapping(
        value = "/{memberId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MemberDetailInternalResponse> getMember(
        @PathVariable UUID memberId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberInternalService.getMember(memberId));
    }

}
