package com.jong.msaboard.support.infra.service;

import com.jong.msaboard.support.infra.cache.RedisCacheable;
import com.jong.msaboard.support.infra.cache.RedisEvict;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheTestService {

    @RedisCacheable(name = "test::", key = "#data.property1")
    public Data saveToRedis(Data data) {
        log.info("Method(cacheable) Parameter : {}", data);
        return data;
    }

    @RedisEvict(name = "test::", key = "#data.property1")
    public void evictFromRedis(Data data) {
        log.info("Method(evict) Parameter : {}", data);
    }

    public record Data(
        String property1,
        String property2,
        String property3
    ) {}

}
