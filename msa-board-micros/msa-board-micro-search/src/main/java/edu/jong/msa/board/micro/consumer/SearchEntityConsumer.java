package edu.jong.msa.board.micro.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.jong.msa.board.common.BoardConstants.ServiceNames;
import edu.jong.msa.board.common.BoardConstants.TopicNames;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.domain.entity.MemberEntity;
import edu.jong.msa.board.domain.repository.MemberEntityRepository;
import edu.jong.msa.board.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SearchEntityConsumer {

	private final MemberEntityRepository memberEntityRepository;

	@KafkaListener(topics = TopicNames.MEMBER_TOPIC, 
			groupId = ServiceNames.SEARCH_SERVICE)
	public void consumeMemberTopic(String message) {
		try {
			TypeReference<KafkaMessage<MemberEntity>> type = new TypeReference<KafkaMessage<MemberEntity>>() {};
			memberEntityRepository.save(ObjectMapperUtils.toObject(message, type).getData());
		} catch (Exception e) {
			log.error("Kafka Consume Error!!! (Message : {})", message);
		}
	}

}
