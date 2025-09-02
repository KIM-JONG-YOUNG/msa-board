package com.jong.msa.board.support.domain;

import com.jong.msa.board.support.domain.document.CommentDocument;
import com.jong.msa.board.support.domain.document.MemberDocument;
import com.jong.msa.board.support.domain.document.PostDocument;
import com.jong.msa.board.support.domain.entity.CommentEntity;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.entity.QCommentEntity;
import com.jong.msa.board.support.domain.entity.QMemberEntity;
import com.jong.msa.board.support.domain.entity.QPostEntity;
import com.jong.msa.board.support.domain.repository.CommentDocumentRepository;
import com.jong.msa.board.support.domain.repository.CommentEntityRepository;
import com.jong.msa.board.support.domain.repository.MemberDocumentRepository;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostDocumentRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = DomainTestContext.class)
public class ElasticsearchTest {

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private CommentEntityRepository commentEntityRepository;

    @Autowired
    private MemberDocumentRepository memberDocumentRepository;

    @Autowired
    private PostDocumentRepository postDocumentRepository;

    @Autowired
    private CommentDocumentRepository commentDocumentRepository;

    @Test
    void 엘라스틱서치_색인() {

        QMemberEntity memberEntity = QMemberEntity.memberEntity;
        QPostEntity postEntity = QPostEntity.postEntity;
        QCommentEntity commentEntity = QCommentEntity.commentEntity;

        List<MemberEntity> memberEntities = memberEntityRepository.findAll();
        List<PostEntity> postEntities = jpaQueryFactory.selectFrom(postEntity)
            .leftJoin(postEntity.writer, memberEntity).fetchJoin()
            .fetch();
        List<CommentEntity> commentEntities = jpaQueryFactory.selectFrom(commentEntity)
            .leftJoin(commentEntity.writer, memberEntity).fetchJoin()
            .leftJoin(commentEntity.post, postEntity).fetchJoin()
            .leftJoin(commentEntity.parent).fetchJoin()
            .fetch();

        List<MemberDocument> memberDocuments = memberEntities.stream()
            .map(entity -> MemberDocument.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .name(entity.getName())
                .gender(entity.getGender())
                .email(entity.getEmail())
                .group(entity.getGroup())
                .createdDateTime(entity.getCreatedDateTime())
                .updatedDateTime(entity.getUpdatedDateTime())
                .state(entity.getState())
                .build())
            .collect(Collectors.toList());

        List<PostDocument> postDocuments = postEntities.stream()
            .map(entity -> PostDocument.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .views(entity.getViews())
                .writer(PostDocument.Writer.builder()
                    .id(entity.getWriter().getId())
                    .username(entity.getWriter().getUsername())
                    .name(entity.getWriter().getName())
                    .gender(entity.getWriter().getGender())
                    .email(entity.getWriter().getEmail())
                    .group(entity.getWriter().getGroup())
                    .createdDateTime(entity.getWriter().getCreatedDateTime())
                    .updatedDateTime(entity.getWriter().getUpdatedDateTime())
                    .state(entity.getWriter().getState())
                    .build())
                .createdDateTime(entity.getCreatedDateTime())
                .updatedDateTime(entity.getUpdatedDateTime())
                .state(entity.getState())
                .build())
            .collect(Collectors.toList());

        List<CommentDocument> commentDocuments = commentEntities.stream()
            .map(entity -> CommentDocument.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .writer(CommentDocument.Writer.builder()
                    .id(entity.getWriter().getId())
                    .username(entity.getWriter().getUsername())
                    .name(entity.getWriter().getName())
                    .gender(entity.getWriter().getGender())
                    .email(entity.getWriter().getEmail())
                    .group(entity.getWriter().getGroup())
                    .createdDateTime(entity.getWriter().getCreatedDateTime())
                    .updatedDateTime(entity.getWriter().getUpdatedDateTime())
                    .state(entity.getWriter().getState())
                    .build())
                .post(CommentDocument.Post.builder()
                    .id(entity.getPost().getId())
                    .title(entity.getPost().getTitle())
                    .content(entity.getPost().getContent())
                    .views(entity.getPost().getViews())
                    .createdDateTime(entity.getPost().getCreatedDateTime())
                    .updatedDateTime(entity.getPost().getUpdatedDateTime())
                    .state(entity.getPost().getState())
                    .build())
                .parent(entity.getParent() == null
                    ? null
                    : CommentDocument.Parent.builder()
                        .id(entity.getParent().getId())
                        .content(entity.getParent().getContent())
                        .createdDateTime(entity.getParent().getCreatedDateTime())
                        .updatedDateTime(entity.getParent().getUpdatedDateTime())
                        .state(entity.getParent().getState())
                        .build())
                .createdDateTime(entity.getCreatedDateTime())
                .updatedDateTime(entity.getUpdatedDateTime())
                .state(entity.getState())
                .build())
            .collect(Collectors.toList());

        memberDocumentRepository.saveAll(memberDocuments);
        postDocumentRepository.saveAll(postDocuments);
        commentDocumentRepository.saveAll(commentDocuments);
    }

}
