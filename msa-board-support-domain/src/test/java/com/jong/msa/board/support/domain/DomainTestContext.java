package com.jong.msa.board.support.domain;

import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.utils.PortUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.utility.DockerImageName;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisServer;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

@Slf4j
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = PackageNames.ROOT_PACKAGE)
public class DomainTestContext {

    public static final int EMBEDDED_REDIS_SERVER_PORT;
    public static final int ELASTICSEARCH_CONTAINER_PORT;

    public static final ElasticsearchContainer ELASTICSEARCH_CONTAINER;
    public static final RedisServer EMBEDDED_REDIS_SERVER;

    static {

        EMBEDDED_REDIS_SERVER_PORT = PortUtils.getAvailablePort();
        EMBEDDED_REDIS_SERVER = RedisServer.builder()
            .redisExecProvider(RedisExecProvider.defaultProvider()
                .override(OS.MAC_OS_X, Architecture.x86_64, "redis/redis-server")
                .override(OS.MAC_OS_X, Architecture.x86, "redis/redis-server"))
            .port(EMBEDDED_REDIS_SERVER_PORT)
            .setting("maxmemory 128mb") // 메모리 최대 사용량 설정
            .build();

        ELASTICSEARCH_CONTAINER_PORT = PortUtils.getAvailablePort();
        (ELASTICSEARCH_CONTAINER = new ElasticsearchContainer(DockerImageName
            .parse("elasticsearch-nori:8.12.1")
            .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch"))
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withExposedPorts(9200)
            .withReuse(true))
            .setPortBindings(List.of(ELASTICSEARCH_CONTAINER_PORT + ":9200"));

        String elasticsearchURL = "localhost:" + ELASTICSEARCH_CONTAINER_PORT;
        System.setProperty("spring.elasticsearch.uris", elasticsearchURL);
        System.setProperty("spring.data.redis.host", "localhost");
        System.setProperty("spring.data.redis.port", String.valueOf(EMBEDDED_REDIS_SERVER_PORT));
        System.setProperty("spring.data.redis.reactive", "false");
    }

    @PostConstruct
    public void init() {
        startEmbeddedRedisServer();
        startElasticsearchContainer();
    }

    @PreDestroy
    public void destroy() {
        stopEmbeddedRedisServer();
        stopElasticsearchContainer();
    }

    private void startEmbeddedRedisServer() {
        log.info("Embedded Redis Server Starting...");
        EMBEDDED_REDIS_SERVER.start();
        log.info("Embedded Redis Server Started. (port: {})", EMBEDDED_REDIS_SERVER_PORT);
    }

    private void stopEmbeddedRedisServer() {
        log.info("Embedded Redis Server Stoping... (port: {})", EMBEDDED_REDIS_SERVER_PORT);
        EMBEDDED_REDIS_SERVER.stop();
        log.info("Embedded Redis Server Stoped.");
    }

    private void startElasticsearchContainer() {
        log.info("Elasticsearch Container Starting...");
        ELASTICSEARCH_CONTAINER.start();
        log.info("Elasticsearch Container Started. (port: {})", ELASTICSEARCH_CONTAINER_PORT);
    }

    private void stopElasticsearchContainer() {
        log.info("Elasticsearch Container Stoping... (port: {})", ELASTICSEARCH_CONTAINER_PORT);
        ELASTICSEARCH_CONTAINER.stop();
        log.info("Elasticsearch Container Stoped.");
    }

}
