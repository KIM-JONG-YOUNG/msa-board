package com.jong.msa.board.microservice.post.service;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.common.constants.RedisPrefixes;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.core.redis.annotation.RedisCacheEvict;
import com.jong.msa.board.core.redis.annotation.RedisCacheable;
import com.jong.msa.board.microservice.post.exception.PostServiceException;
import com.jong.msa.board.microservice.post.mapper.PostEntityMapper;
import com.jong.msa.board.microservice.post.request.PostCreateRequest;
import com.jong.msa.board.microservice.post.request.PostModifyRequest;
import com.jong.msa.board.microservice.post.response.PostDetailsResponse;
import com.jong.msa.board.support.domain.annotation.DistributeTransactional;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.entity.QMemberEntity;
import com.jong.msa.board.support.domain.entity.QPostEntity;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCoreService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberEntityRepository memberEntityRepository;

    private final PostEntityRepository postEntityRepository;

    private final PostEntityMapper postEntityMapper;

    private final JPAQueryFactory jpaQueryFactory;

    private void sendToKafka(UUID id) {
        String topic = KafkaTopicNames.POST_TOPIC;
        String message = id.toString();
        KafkaSendEvent event = new KafkaSendEvent(topic, message);
        applicationEventPublisher.publishEvent(event);
    }

    @Transactional
    public UUID create(PostCreateRequest request) {

        MemberEntity writer = memberEntityRepository.findById(request.writerId())
            .orElseThrow(PostServiceException::notFoundPostWriter);

        PostEntity entity = postEntityMapper.toEntity(request);
        PostEntity relatedEntity = entity.withWriter(writer);
        PostEntity savedEntity = postEntityRepository.save(relatedEntity);

        sendToKafka(savedEntity.getId());

        return savedEntity.getId();
    }

    @RedisCacheEvict(name = RedisPrefixes.POST_PREFIX, key = "#id")
    @DistributeTransactional(name = RedisPrefixes.POST_LOCK_PREFIX, key = "#id")
    public void modify(UUID id, PostModifyRequest request) {

        PostEntity entity = postEntityRepository.findById(id)
            .orElseThrow(PostServiceException::notFoundPostWriter);

        PostEntity updatedEntity = postEntityMapper.updateEntity(entity, request);
        PostEntity savedEntity = postEntityRepository.save(updatedEntity);

        sendToKafka(savedEntity.getId());
    }

    @RedisCacheEvict(name = RedisPrefixes.POST_PREFIX, key = "#id")
    @DistributeTransactional(name = RedisPrefixes.POST_LOCK_PREFIX, key = "#id")
    public void increaseViews(UUID id) {

        PostEntity entity = postEntityRepository.findById(id)
            .orElseThrow(PostServiceException::notFoundPostWriter);

        PostEntity increasedEntity = entity.increaseView();
        PostEntity savedEntity = postEntityRepository.save(increasedEntity);

        sendToKafka(savedEntity.getId());
    }

    @RedisCacheable(name = RedisPrefixes.POST_PREFIX, key = "#id")
    @Transactional(readOnly = true)
    public PostDetailsResponse get(UUID id) {

        QMemberEntity memberEntity = QMemberEntity.memberEntity;
        QPostEntity postEntity = QPostEntity.postEntity;

        PostEntity entity = jpaQueryFactory
            .selectFrom(postEntity)
            .leftJoin(postEntity.writer, memberEntity).fetchJoin()
            .where(postEntity.id.eq(id))
            .fetchOne();

        if (entity == null) {
            throw PostServiceException.notFoundPost();
        }
        return postEntityMapper.toDetail(entity);
    }

}
