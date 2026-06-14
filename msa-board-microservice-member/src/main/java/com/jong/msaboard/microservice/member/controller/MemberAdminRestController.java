package com.jong.msaboard.microservice.member.controller;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.microservice.member.request.admin.MemberGroupModifyAdminRequest;
import com.jong.msaboard.microservice.member.request.admin.MemberStatusModifyAdminRequest;
import com.jong.msaboard.microservice.member.response.admin.MemberDetailAdminResponse;
import com.jong.msaboard.microservice.member.service.MemberAdminService;
import com.jong.msaboard.support.web.constants.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/admin/members")
public class MemberAdminRestController {

    private final MemberAdminService memberAdminService;

    @Operation(summary = "특정 회원 그룹 수정")
    @PreAuthorize(SecurityExpressions.IS_ADMIN)
    @PatchMapping(
        value = "/{memberId}/group",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> modifyMemberGroup(
        @PathVariable UUID memberId,
        @RequestBody @Valid MemberGroupModifyAdminRequest request
    ) {
        memberAdminService.modifyMemberGroup(memberId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.MEMBER_ID, memberId.toString())
            .build();
    }

    @Operation(summary = "특정 회원 상태 수정")
    @PreAuthorize(SecurityExpressions.IS_ADMIN)
    @PatchMapping(
        value = "/{memberId}/status",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> modifyMemberStatus(
        @PathVariable UUID memberId,
        @RequestBody @Valid MemberStatusModifyAdminRequest request
    ) {
        memberAdminService.modifyMemberStatus(memberId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.MEMBER_ID, memberId.toString())
            .build();
    }

    @Operation(summary = "특정 회원 상세 조회")
    @PreAuthorize(SecurityExpressions.IS_ADMIN)
    @GetMapping(
        value = "/{memberId}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MemberDetailAdminResponse> getMember(
        @PathVariable UUID memberId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberAdminService.getMember(memberId));
    }

}
