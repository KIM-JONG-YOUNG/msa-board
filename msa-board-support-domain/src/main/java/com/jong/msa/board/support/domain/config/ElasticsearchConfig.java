package com.jong.msa.board.support.domain.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.constants.PackageNames;
import com.jong.msa.board.common.converter.LocalDateConverter;
import com.jong.msa.board.common.converter.LocalDateTimeConverter;
import com.jong.msa.board.common.converter.LocalTimeConverter;
import com.jong.msa.board.support.domain.repository.AbstractDocumentRepository;
import java.util.List;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchCustomConversions;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@ConditionalOnClass(name = "org.springframework.data.elasticsearch.repository.ElasticsearchRepository")
@EnableConfigurationProperties(ElasticsearchProperties.class)
@EnableElasticsearchRepositories(
    basePackages = PackageNames.ROOT_PACKAGE,
    includeFilters = {
        @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = AbstractDocumentRepository.class)
    })
public class ElasticsearchConfig {

    @Bean
    RestClient elasticsearchRestClient(ElasticsearchProperties properties) {
        return RestClient.builder(properties.getUris().stream()
                .map(HttpHost::create)
                .toArray(HttpHost[]::new))
            .build();
    }

    @Bean
    ElasticsearchTransport elasticsearchTransport(RestClient restClient, ObjectMapper objectMapper) {
        JacksonJsonpMapper jsonpMapper = new JacksonJsonpMapper(objectMapper);
        return new RestClientTransport(restClient, jsonpMapper);
    }

    @Bean
    ElasticsearchClient elasticsearchClient(ElasticsearchTransport transport) {
        return new ElasticsearchClient(transport);
    }

    @Bean
    ElasticsearchCustomConversions elasticsearchCustomConversions() {
        return new ElasticsearchCustomConversions(List.of(
            LocalTimeConverter.StringToLocalTime.INSTANCE,
            LocalTimeConverter.LocalTimeToString.INSTANCE,
            LocalDateConverter.StringToLocalDate.INSTANCE,
            LocalDateConverter.LocalDateToString.INSTANCE,
            LocalDateTimeConverter.StringToLocalDateTime.INSTANCE,
            LocalDateTimeConverter.LocalDateTimeToString.INSTANCE
        ));
    }

}
