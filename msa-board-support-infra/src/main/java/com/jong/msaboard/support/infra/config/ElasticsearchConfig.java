package com.jong.msaboard.support.infra.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.support.infra.condition.ConditionalOnElasticsearch;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@ConditionalOnElasticsearch
public class ElasticsearchConfig {

    @Bean
    @Primary
    RestClient elasticsearchRestClient(ElasticsearchProperties properties) {
        return RestClient.builder(properties.getUris().stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new))
            .build();
    }

    @Bean
    @Primary
    ElasticsearchTransport elasticsearchTransport(RestClient elasticsearchRestClient, ObjectMapper objectMapper) {
        return new RestClientTransport(elasticsearchRestClient, new JacksonJsonpMapper(objectMapper));
    }

    @Bean
    @Primary
    ElasticsearchClient elasticsearchClient(ElasticsearchTransport elasticsearchTransport) {
        return new ElasticsearchClient(elasticsearchTransport);
    }

}
