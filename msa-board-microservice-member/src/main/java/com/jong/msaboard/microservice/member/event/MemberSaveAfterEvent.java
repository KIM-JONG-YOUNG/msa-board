package com.jong.msaboard.microservice.member.event;

import com.jong.msaboard.common.constants.KafkaTopicNames;
import com.jong.msaboard.common.constants.RedisKeyPrefixes;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

public record MemberSaveAfterEvent(UUID memberId) {

    @Component
    @RequiredArgsConstructor
    public static class Listener {

        private final RedisTemplate<String, String> redisTemplate;
        private final KafkaTemplate<String, String> kafkaTemplate;

        @Async
        @TransactionalEventListener
        public void onEvent(MemberSaveAfterEvent event) {
            redisTemplate.delete(RedisKeyPrefixes.MEMBER + event.memberId());
            kafkaTemplate.send(KafkaTopicNames.MEMBER_SAVE, event.memberId().toString());
        }
    }

}
