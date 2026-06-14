package com.jong.msaboard.microservice.member.service;

import com.jong.msaboard.microservice.member.mapper.MemberEntityMapper;
import com.jong.msaboard.microservice.member.repository.MemberRepository;
import com.jong.msaboard.microservice.member.response.internal.MemberDetailInternalResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberInternalService {

    private final MemberRepository memberRepository;
    private final MemberEntityMapper memberEntityMapper;

    @Transactional(readOnly = true)
    public MemberDetailInternalResponse getMember(UUID memberId) {
        var member = memberRepository.findByIdOrThrow(memberId);
        return memberEntityMapper.toDetailInternalResponse(member);
    }

}
