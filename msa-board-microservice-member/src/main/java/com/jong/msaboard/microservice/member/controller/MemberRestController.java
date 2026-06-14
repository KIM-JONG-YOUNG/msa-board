package com.jong.msaboard.microservice.member.controller;

import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.microservice.member.request.MemberJoinRequest;
import com.jong.msaboard.microservice.member.request.MemberLoginRequest;
import com.jong.msaboard.microservice.member.request.MemberModifyRequest;
import com.jong.msaboard.microservice.member.request.MemberPasswordModifyRequest;
import com.jong.msaboard.microservice.member.response.MemberDetailResponse;
import com.jong.msaboard.microservice.member.service.MemberService;
import com.jong.msaboard.support.web.constants.SecurityExpressions;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/members")
public class MemberRestController {

    private final MemberService memberService;

    @Operation(summary = "회원 가입")
    @PreAuthorize(SecurityExpressions.IS_ANONYMOUS)
    @PostMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> joinMember(
        @RequestBody @Valid MemberJoinRequest request
    ) {
        var memberId = memberService.joinMember(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HeaderNames.MEMBER_ID, memberId.toString())
            .build();
    }

    @Operation(summary = "현재 회원 정보 수정")
    @PreAuthorize(SecurityExpressions.IS_AUTHENTICATED)
    @PutMapping(
        value = "/me",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> modifyCurrentMember(
        @AuthenticationPrincipal UUID memberId,
        @RequestBody @Valid MemberModifyRequest request
    ) {
        memberService.modifyMember(memberId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.MEMBER_ID, memberId.toString())
            .build();
    }

    @Operation(summary = "현재 회원 비밀번호 수정")
    @PreAuthorize(SecurityExpressions.IS_AUTHENTICATED)
    @PatchMapping(
        value = "/me/password",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> modifyCurrentMemberPassword(
        @AuthenticationPrincipal UUID memberId,
        @RequestBody @Valid MemberPasswordModifyRequest request
    ) {
        memberService.modifyMemberPassword(memberId, request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.MEMBER_ID, memberId.toString())
            .build();
    }

    @Operation(summary = "현재 회원 상세 조회")
    @PreAuthorize(SecurityExpressions.IS_AUTHENTICATED)
    @GetMapping(
        value = "/me",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<MemberDetailResponse> getCurrentMember(
        @AuthenticationPrincipal UUID memberId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(memberService.getMember(memberId));
    }

    @Operation(summary = "현재 회원 탈퇴")
    @PreAuthorize(SecurityExpressions.IS_AUTHENTICATED)
    @DeleteMapping(
        value = "/me",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> leaveCurrentMember(
        @AuthenticationPrincipal UUID memberId
    ) {
        memberService.leaveMember(memberId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "회원 로그인")
    @PreAuthorize(SecurityExpressions.IS_ANONYMOUS)
    @PostMapping(
        value = "/auth/login",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> loginMemberAuth(
        @RequestBody @Valid MemberLoginRequest request
    ) {
        var memberTokens = memberService.loginMemberAuth(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.ACCESS_TOKEN, memberTokens.accessToken())
            .header(HeaderNames.REFRESH_TOKEN, memberTokens.refreshToken())
            .build();
    }

    @Operation(summary = "회원 로그아웃")
    @PreAuthorize(SecurityExpressions.IS_AUTHENTICATED)
    @PostMapping(
        value = "/auth/logout",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> logoutMemberAuth(
        @RequestHeader(HeaderNames.ACCESS_TOKEN) String accessToken
    ) {
        memberService.logoutMemberAuth(accessToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "회원 인증 토큰 갱신")
    @PostMapping(
        value = "/auth/refresh",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Void> refreshMemberAuth(
        @RequestHeader(HeaderNames.REFRESH_TOKEN) String refreshToken
    ) {
        var memberTokens = memberService.refreshMemberAuth(refreshToken);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
            .header(HeaderNames.ACCESS_TOKEN, memberTokens.accessToken())
            .header(HeaderNames.REFRESH_TOKEN, memberTokens.refreshToken())
            .build();
    }

}
