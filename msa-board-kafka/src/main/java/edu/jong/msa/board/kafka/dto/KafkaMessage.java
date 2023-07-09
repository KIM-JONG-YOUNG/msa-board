package edu.jong.msa.board.kafka.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class KafkaMessage<T> {

	private String topic;

	private T data;

	private LocalDateTime sendTime;

	public KafkaMessage(String topic, T data) {
		super();
		this.topic = topic;
		this.data = data;
		this.sendTime = LocalDateTime.now();
	}
	
}
