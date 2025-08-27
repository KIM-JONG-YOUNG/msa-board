package com.jong.msa.board.microservice.search.consumer;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.microservice.search.feign.CommentFeignClient;
import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.feign.PostFeignClient;
import com.jong.msa.board.microservice.search.mapper.CommentDocumentMapper;
import com.jong.msa.board.support.domain.document.CommentDocument;
import com.jong.msa.board.support.domain.repository.CommentDocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentTopicConsumer {

    private final MemberFeignClient memberFeignClient;

    private final PostFeignClient postFeignClient;

    private final CommentFeignClient commentFeignClient;

    private final CommentDocumentRepository commentDocumentRepository;

    private final CommentDocumentMapper commentDocumentMapper;

    @KafkaListener(
        topics = KafkaTopicNames.POST_TOPIC,
        groupId = MicroserviceNames.SEARCH_MICROSERVICE)
    public void handleMemberTopic(String idString) {

        UUID id = UUID.fromString(idString);

        CommentFeignClient.GetResponse comment = commentFeignClient.get(id);
        PostFeignClient.GetResponse post = postFeignClient.get(comment.postId());
        MemberFeignClient.GetResponse writer = memberFeignClient.get(comment.writer().id());
        CommentFeignClient.GetResponse parent = comment.parentId() != null
            ? commentFeignClient.get(comment.parentId())
            : null;

        CommentDocument document = commentDocumentMapper.toDocument(comment);
        CommentDocument.Writer documentWriter = commentDocumentMapper.toDocumentWriter(writer);
        CommentDocument.Post documentPost = commentDocumentMapper.toDocumentPost(post);
        CommentDocument.Parent documentParent = commentDocumentMapper.toDocumentParent(parent);

        commentDocumentRepository.save(document
            .withWriter(documentWriter)
            .withPost(documentPost)
            .withParent(documentParent));
    }

}
