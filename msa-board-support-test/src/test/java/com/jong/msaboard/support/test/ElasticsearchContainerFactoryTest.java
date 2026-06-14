package com.jong.msaboard.support.test;

import com.jong.msaboard.support.test.factory.ElasticsearchContainerFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@SpringJUnitConfig
public class ElasticsearchContainerFactoryTest {

    @Container
    static ElasticsearchContainer CONTAINER = ElasticsearchContainerFactory.createElasticsearchContainer();

    @Test
    void Elasticsearch_Test_Container_실행_테스트() {
        log.info("Elasticsearch Container IP  : {}", CONTAINER.getContainerIpAddress());
        log.info("Elasticsearch Container Port: {}", CONTAINER.getFirstMappedPort());
        log.info("Elasticsearch Container URL : {}", CONTAINER.getHttpHostAddress());
    }

}
