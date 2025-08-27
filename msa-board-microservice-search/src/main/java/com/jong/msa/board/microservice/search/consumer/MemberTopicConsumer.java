package com.jong.msa.board.microservice.search.consumer;

import com.jong.msa.board.common.constants.KafkaTopicNames;
import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.microservice.search.feign.MemberFeignClient;
import com.jong.msa.board.microservice.search.mapper.MemberDocumentMapper;
import com.jong.msa.board.support.domain.document.MemberDocument;
import com.jong.msa.board.support.domain.repository.MemberDocumentRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberTopicConsumer {

    private final MemberFeignClient memberFeignClient;

    private final MemberDocumentRepository memberDocumentRepository;

    private final MemberDocumentMapper memberDocumentMapper;

    @KafkaListener(
        topics = KafkaTopicNames.MEMBER_TOPIC,
        groupId = MicroserviceNames.SEARCH_MICROSERVICE)
    public void handleMemberTopic(String idString) {
        UUID id = UUID.fromString(idString);
        MemberFeignClient.GetResponse member = memberFeignClient.get(id);
        MemberDocument document = memberDocumentMapper.toDocument(member);
        memberDocumentRepository.save(document);
    }

}
