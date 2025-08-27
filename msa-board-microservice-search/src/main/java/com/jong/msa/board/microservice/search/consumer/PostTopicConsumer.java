package com.jong.msa.board.microservice.search.consumer;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.feign.PostFeignClient;
import com.jong.msa.board.microservice.search.mapper.PostDocumentMapper;
import com.jong.msa.board.support.domain.document.PostDocument;
import com.jong.msa.board.support.domain.repository.PostDocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostTopicConsumer {

    private final MemberFeignClient memberFeignClient;

    private final PostFeignClient postFeignClient;

    private final PostDocumentRepository postDocumentRepository;

    private final PostDocumentMapper postDocumentMapper;

    @KafkaListener(
        topics = KafkaTopicNames.POST_TOPIC,
        groupId = MicroserviceNames.SEARCH_MICROSERVICE)
    public void handleMemberTopic(String idString) {

        UUID id = UUID.fromString(idString);

        PostFeignClient.GetResponse post = postFeignClient.get(id);
        MemberFeignClient.GetResponse writer = memberFeignClient.get(post.writer().id());

        PostDocument document = postDocumentMapper.toDocument(post);
        PostDocument.Writer documentWriter = postDocumentMapper.toDocumentWriter(writer);

        postDocumentRepository.save(document.withWriter(documentWriter));
    }

}
