package com.jong.msaboard.support.infra.factory;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticsearchContainerFactory {

    public static ElasticsearchContainer create() {
        return new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.12.1")
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withReuse(true);
    }

}
