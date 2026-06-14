package com.jong.msaboard.microservice.member.service;

import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import com.jong.msaboard.common.type.Status;
import com.jong.msaboard.microservice.member.error.MemberErrorCode;
import com.jong.msaboard.microservice.member.event.MemberSaveAfterEvent;
import com.jong.msaboard.microservice.member.mapper.MemberEntityMapper;
import com.jong.msaboard.microservice.member.repository.MemberRepository;
import com.jong.msaboard.microservice.member.request.MemberJoinRequest;
import com.jong.msaboard.microservice.member.request.MemberLoginRequest;
import com.jong.msaboard.microservice.member.request.MemberModifyRequest;
import com.jong.msaboard.microservice.member.request.MemberPasswordModifyRequest;
import com.jong.msaboard.microservice.member.response.MemberDetailResponse;
import com.jong.msaboard.support.infra.cache.RedisCacheable;
import com.jong.msaboard.support.infra.cache.RedisEvict;
import com.jong.msaboard.support.infra.transaction.LockTransactional;
import com.jong.msaboard.support.web.service.TokenMvcService;
import java.util.UUID;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordEncoder passwordEncoder;

    private final MemberRepository memberRepository;
    private final MemberEntityMapper memberEntityMapper;
    private final TokenMvcService tokenMvcService;

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#request.username")
    public UUID joinMember(MemberJoinRequest request) {
        if (memberRepository.existsByUsername(request.username())) {
            throw MemberErrorCode.ALREADY_EXISTS_MEMBER_USERNAME.toException();
        }
        var member = memberEntityMapper.toEntity(request, passwordEncoder);
        var savedMember = memberRepository.save(member);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
        return savedMember.getId();
    }

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#memberId")
    public void modifyMember(UUID memberId, MemberModifyRequest request) {
        var member = memberRepository.findByIdAndActiveOrThrow(memberId);
        var updatedMember = memberEntityMapper.updateEntity(request, member);
        var savedMember = memberRepository.save(updatedMember);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
    }

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#memberId")
    @RedisEvict(name = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX, key = "#memberId")
    public void modifyMemberPassword(UUID memberId, MemberPasswordModifyRequest request) {
        var member = memberRepository.findByIdAndActiveOrThrow(memberId);
        var updatedMember = memberEntityMapper.updateEntity(request, member, passwordEncoder);
        var savedMember = memberRepository.save(updatedMember);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
    }

    @Transactional(readOnly = true)
    @RedisCacheable(name = RedisKeyPrefixes.MEMBER, key = "#memberId")
    public MemberDetailResponse getMember(UUID memberId) {
        var member = memberRepository.findByIdAndActiveOrThrow(memberId);
        return memberEntityMapper.toDetailResponse(member);
    }

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#memberId")
    @RedisEvict(name = RedisKeyPrefixes.WHITELIST_TOKEN_PREFIX, key = "#memberId")
    public void leaveMember(UUID memberId) {
        var member = memberRepository.findByIdAndActiveOrThrow(memberId);
        var updatedMember = member.setStatus(Status.INACTIVE);
        var savedMember = memberRepository.save(updatedMember);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
    }

    @Transactional(readOnly = true)
    public MemberTokens loginMemberAuth(MemberLoginRequest request) {
        var member = memberRepository.findByUsernameAndActiveOrThrow(request.username());
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw MemberErrorCode.NOT_MATCHED_MEMBER_PASSWORD.toException();
        }
        return MemberTokens.builder()
            .accessToken(tokenMvcService.generateAccessToken(member.getId(), member.getGroup()))
            .refreshToken(tokenMvcService.generateRefreshToken(member.getId()))
            .build();
    }

    public void logoutMemberAuth(String accessToken) {
        tokenMvcService.revokeAccessToken(accessToken);
    }

    @Transactional(readOnly = true)
    public MemberTokens refreshMemberAuth(String refreshToken) {
        var memberId = tokenMvcService.getMemberIdFromRefreshToken(refreshToken);
        var member = memberRepository.findByIdAndActiveOrThrow(memberId);
        var memberTokens = MemberTokens.builder()
            .accessToken(tokenMvcService.generateAccessToken(member.getId(), member.getGroup()))
            .refreshToken(tokenMvcService.generateRefreshToken(member.getId()))
            .build();
        tokenMvcService.revokeRefreshToken(refreshToken);
        return memberTokens;
    }

    @Builder
    public record MemberTokens(
        String accessToken,
        String refreshToken
    ) {}

}
