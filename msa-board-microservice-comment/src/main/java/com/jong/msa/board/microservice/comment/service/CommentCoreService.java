package com.jong.msa.board.microservice.comment.service;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.microservice.comment.exception.CommentServiceException;
import com.jong.msa.board.microservice.comment.mapper.CommentEntityMapper;
import com.jong.msa.board.microservice.comment.request.CommentCreateRequest;
import com.jong.msa.board.microservice.comment.response.CommentDetailsResponse;
import com.jong.msa.board.microservice.comment.response.CommentTreeResponse;
import com.jong.msa.board.support.domain.entity.CommentEntity;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.entity.QCommentEntity;
import com.jong.msa.board.support.domain.entity.QMemberEntity;
import com.jong.msa.board.support.domain.entity.QPostEntity;
import com.jong.msa.board.support.domain.repository.CommentEntityRepository;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCoreService {

    private final ApplicationEventPublisher applicationEventPublisher;

    private final MemberEntityRepository memberEntityRepository;

    private final PostEntityRepository postEntityRepository;

    private final CommentEntityRepository commentEntityRepository;

    private final CommentEntityMapper commentEntityMapper;

    private final JPAQueryFactory jpaQueryFactory;

    private void sendToKafka(UUID id) {
        String topic = KafkaTopicNames.COMMENT_TOPIC;
        String message = id.toString();
        KafkaSendEvent event = new KafkaSendEvent(topic, message);
        applicationEventPublisher.publishEvent(event);
    }

    public void create(CommentCreateRequest request) {

        UUID writerId = request.writerId();
        UUID postId = request.postId();
        UUID parentId = request.parentId();

        MemberEntity writer = memberEntityRepository.findById(writerId)
            .orElseThrow(CommentServiceException::notFoundCommentWriter);
        PostEntity post = postEntityRepository.findById(postId)
            .orElseThrow(CommentServiceException::notFoundCommentPost);
        CommentEntity parent = parentId == null ? null
            : commentEntityRepository.findById(parentId)
                .orElseThrow(CommentServiceException::notFoundCommentParent);

        CommentEntity entity = commentEntityMapper.toEntity(request);
        CommentEntity relatedEntity = entity.withWriter(writer).withPost(post).withParent(parent);
        CommentEntity savedEntity = commentEntityRepository.save(relatedEntity);

        sendToKafka(savedEntity.getId());
    }

    @Transactional(readOnly = true)
    public CommentDetailsResponse get(UUID id) {
        return commentEntityMapper.toDetail(commentEntityRepository.findById(id)
            .orElseThrow(CommentServiceException::notFoundComment));
    }

    @Transactional(readOnly = true)
    public CommentTreeResponse getTree(UUID postId) {

        QMemberEntity memberEntity = QMemberEntity.memberEntity;
        QPostEntity postEntity = QPostEntity.postEntity;
        QCommentEntity commentEntity = QCommentEntity.commentEntity;

        List<CommentEntity> comments = jpaQueryFactory
            .selectFrom(commentEntity)
            .leftJoin(commentEntity.writer, memberEntity).fetchJoin()
            .leftJoin(commentEntity.post, postEntity).fetchJoin()
            .leftJoin(commentEntity.parent).fetchJoin()
            .where(commentEntity.post.id.eq(postId))
            .orderBy(
                commentEntity.parent.id.asc().nullsFirst(),
                commentEntity.createdDateTime.desc())
            .fetch();

        Map<UUID, List<CommentTreeResponse.Item>> childrenMap = new HashMap<>();
        comments.stream()
            .filter(comment -> comment.getParent() != null)
            .forEach(comment -> {
                CommentEntity parent = comment.getParent();
                List<CommentTreeResponse.Item> siblings = null;
                siblings = childrenMap.getOrDefault(parent.getId(), new ArrayList<>());
                siblings.add(commentEntityMapper.toTreeItem(comment));
                childrenMap.put(parent.getId(), siblings);
            });

        return CommentTreeResponse.builder()
            .totalCount(comments.size())
            .list(comments.stream()
                .filter(comment -> comment.getParent() == null)
                .map(comment -> {
                    CommentTreeResponse.Item item = commentEntityMapper.toTreeItem(comment);
                    List<CommentTreeResponse.Item> children = getChildren(childrenMap, comment.getId());
                    item.children().addAll(children);
                    return item;
                })
                .toList())
            .build();
    }

    private List<CommentTreeResponse.Item> getChildren(
        Map<UUID, List<CommentTreeResponse.Item>> childrenMap,
        UUID parentId
    ) {
        List<CommentTreeResponse.Item> children = childrenMap.getOrDefault(parentId, new ArrayList<>());
        children.forEach(child -> child.children().addAll(getChildren(childrenMap, child.id())));
        return children;
    }

}
