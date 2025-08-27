package com.jong.msa.board.microservice.member.service;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.core.redis.annotation.RedisCacheEvict;
import com.jong.msa.board.core.redis.annotation.RedisCacheable;
import com.jong.msa.board.microservice.member.exception.MemberServiceException;
import com.jong.msa.board.microservice.member.mapper.MemberEntityMapper;
import com.jong.msa.board.microservice.member.request.MemberCreateRequest;
import com.jong.msa.board.microservice.member.request.MemberLoginRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyPasswordRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyRequest;
import com.jong.msa.board.microservice.member.response.MemberDetailsResponse;
import com.jong.msa.board.support.domain.annotation.DistributeTransactional;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberCoreService {

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberEntityRepository memberEntityRepository;

    private final MemberEntityMapper memberEntityMapper;

    private void sendToKafka(UUID id) {
        String topic = KafkaTopicNames.MEMBER_TOPIC;
        String message = id.toString();
        KafkaSendEvent event = new KafkaSendEvent(topic, message);
        applicationEventPublisher.publishEvent(event);
    }

    @DistributeTransactional(name = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#request.username")
    public UUID create(MemberCreateRequest request) {

        if (memberEntityRepository.existsByUsername(request.username())) {
            throw MemberServiceException.alreadyExistsMemberUsername();
        }

        String encodedPassword = passwordEncoder.encode(request.password());
        MemberEntity entity = memberEntityMapper.toEntity(request);
        MemberEntity encodedEntity = entity.setPassword(encodedPassword);
        MemberEntity savedEntity = memberEntityRepository.save(encodedEntity);

        sendToKafka(savedEntity.getId());

        return entity.getId();
    }

    @RedisCacheEvict(name = RedisPrefixes.MEMBER_PREFIX, key = "#id")
    @DistributeTransactional(name = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#id")
    public void modify(UUID id, MemberModifyRequest request) {

        MemberEntity entity = memberEntityRepository.findById(id)
            .orElseThrow(MemberServiceException::notFoundMember);

        MemberEntity updatedEntity = memberEntityMapper.updateEntity(entity, request);
        MemberEntity savedEntity = memberEntityRepository.save(updatedEntity);

        sendToKafka(savedEntity.getId());
    }

    @RedisCacheEvict(name = RedisPrefixes.MEMBER_PREFIX, key = "#id")
    @DistributeTransactional(name = RedisPrefixes.MEMBER_LOCK_PREFIX, key = "#id")
    public void modifyPassword(UUID id, MemberModifyPasswordRequest request) {

        MemberEntity entity = memberEntityRepository.findById(id)
            .orElseThrow(MemberServiceException::notFoundMember);

        if (passwordEncoder.matches(request.currentPassword(), entity.getPassword())) {

            String encodedPassword = passwordEncoder.encode(request.newPassword());
            MemberEntity encodedEntity = entity.setPassword(encodedPassword);
            MemberEntity savedEntity = memberEntityRepository.save(encodedEntity);

            sendToKafka(savedEntity.getId());

        } else {
            throw MemberServiceException.notMatchMemberPassword();
        }
    }

    @RedisCacheable(name = RedisPrefixes.MEMBER_PREFIX, key = "#id")
    @Transactional(readOnly = true)
    public MemberDetailsResponse get(UUID id) {
        return memberEntityMapper.toDetails(memberEntityRepository.findById(id)
            .orElseThrow(MemberServiceException::notFoundMember));
    }

    @Transactional(readOnly = true)
    public MemberDetailsResponse login(MemberLoginRequest request) {

        MemberEntity entity = memberEntityRepository.findByUsername(request.username())
            .orElseThrow(MemberServiceException::notFoundMemberUsername);

        if (entity.getState() != State.ACTIVE) {
            throw MemberServiceException.notActiveMember();
        }

        if (passwordEncoder.matches(request.password(), entity.getPassword())) {
            return memberEntityMapper.toDetails(entity);
        } else {
            throw MemberServiceException.notMatchMemberPassword();
        }
    }

}
