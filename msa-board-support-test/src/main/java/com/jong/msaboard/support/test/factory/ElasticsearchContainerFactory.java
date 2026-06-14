package com.jong.msaboard.support.test.factory;

import org.testcontainers.containers.BindMode;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class ElasticsearchContainerFactory {

    public static ElasticsearchContainer createElasticsearchContainer() {
        return new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.12.1")
            .withClasspathResourceMapping(
                "elasticsearch/plugins",
                "/usr/share/elasticsearch/plugins",
                BindMode.READ_ONLY
            )
            .withEnv("ES_JAVA_OPTS", "-Xms512m -Xmx512m")
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withReuse(true);
    }

}
