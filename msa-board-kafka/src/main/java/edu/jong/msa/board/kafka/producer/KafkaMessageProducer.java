package edu.jong.msa.board.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.kafka.dto.KafkaMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {

	private final KafkaTemplate<String, String> kafkaTemplate;
	
	public <T> void send(KafkaMessage<T> message) {
		kafkaTemplate.send(message.getTopic(), ObjectMapperUtils.toJson(message));
	}
	
}
