package com.jong.msa.board.core.redis;

import com.jong.msa.board.core.redis.annotation.RedisCacheEvict;
import com.jong.msa.board.core.redis.annotation.RedisCacheable;
import com.jong.msa.board.core.redis.config.RedisBlockingConfig;
import com.jong.msa.board.core.redis.config.RedisReactiveConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

@Slf4j
@DirtiesContext
@SpringBootTest(
    classes = RedisTestContext.class,
    properties = "spring.data.redis.reactive=true")
public class RedisReactiveTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void 조건부_Bean_등록_테스트() {
        RedisTestContext.assertBeanNotExists(applicationContext, RedisBlockingConfig.class);
        RedisTestContext.assertBeanExists(applicationContext, RedisReactiveConfig.class);
        RedisTestContext.assertBeanNotExists(applicationContext, RedisCacheable.Aspect.class);
        RedisTestContext.assertBeanNotExists(applicationContext, RedisCacheEvict.Aspect.class);
    }

}
