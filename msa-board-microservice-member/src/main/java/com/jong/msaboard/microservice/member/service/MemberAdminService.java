package com.jong.msaboard.microservice.member.service;

import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import com.jong.msaboard.microservice.member.event.MemberSaveAfterEvent;
import com.jong.msaboard.microservice.member.mapper.MemberEntityMapper;
import com.jong.msaboard.microservice.member.repository.MemberRepository;
import com.jong.msaboard.microservice.member.request.admin.MemberGroupModifyAdminRequest;
import com.jong.msaboard.microservice.member.request.admin.MemberStatusModifyAdminRequest;
import com.jong.msaboard.microservice.member.response.admin.MemberDetailAdminResponse;
import com.jong.msaboard.support.infra.transaction.LockTransactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAdminService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberRepository memberRepository;
    private final MemberEntityMapper memberEntityMapper;

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#memberId")
    public void modifyMemberGroup(UUID memberId, MemberGroupModifyAdminRequest request) {
        var member = memberRepository.findByIdOrThrow(memberId);
        var updatedMember = memberEntityMapper.updateEntity(request, member);
        var savedMember = memberRepository.save(updatedMember);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
    }

    @LockTransactional(name = RedisKeyPrefixes.MEMBER_LOCK, key = "#memberId")
    public void modifyMemberStatus(UUID memberId, MemberStatusModifyAdminRequest request) {
        var member = memberRepository.findByIdOrThrow(memberId);
        var updatedMember = memberEntityMapper.updateEntity(request, member);
        var savedMember = memberRepository.save(updatedMember);
        applicationEventPublisher.publishEvent(new MemberSaveAfterEvent(savedMember.getId()));
    }

    @Transactional(readOnly = true)
    public MemberDetailAdminResponse getMember(UUID memberId) {
        var member = memberRepository.findByIdOrThrow(memberId);
        return memberEntityMapper.toDetailAdminResponse(member);
    }

}
