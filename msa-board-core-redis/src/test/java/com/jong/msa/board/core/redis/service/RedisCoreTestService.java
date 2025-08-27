package com.jong.msa.board.core.redis.service;

import com.jong.msa.board.core.redis.annotation.RedisCacheEvict;
import com.jong.msa.board.core.redis.annotation.RedisCacheable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisCoreTestService {

    public static final String PREFIX = "test::";

    @RedisCacheable(name = PREFIX, key = "#dto.key")
    public DTO save(DTO dto) {
        log.info("save() called with dto={}", dto);
        return dto;
    }

    @RedisCacheEvict(name = PREFIX, key = "#key")
    public void remove(String key) {
        log.info("remove() called with key={}", key);
    }

    public record DTO(String key, String value) {}

}
