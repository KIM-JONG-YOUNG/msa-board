package com.jong.msaboard.support.infra;

import static org.junit.jupiter.api.Assertions.assertTrue;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.IndexOperation;
import com.jong.msaboard.support.infra.config.ElasticsearchConfig;
import com.jong.msaboard.support.infra.factory.ElasticsearchContainerFactory;
import com.jong.msaboard.support.infra.factory.ElasticsearchRequestFactory;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Slf4j
@Testcontainers
@DataElasticsearchTest
@ContextConfiguration(classes = {
    ElasticsearchConfig.class,
    ElasticsearchRequestFactory.class,
})
@TestPropertySource(properties = {
    "spring.data.elasticsearch.repositories.enabled=false"
})
public class ElasticsearchRequestFactoryTest {

    @Container
    static ElasticsearchContainer elasticsearchContainer = ElasticsearchContainerFactory.create();

    @Autowired
    ElasticsearchClient elasticsearchClient;

    @Autowired
    ElasticsearchRequestFactory elasticsearchSearchRequestFactory;

    @DynamicPropertySource
    static void initProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.uris", elasticsearchContainer::getHttpHostAddress);
    }

    @Test
    void SearchAfter_테스트() throws Exception {

        var indexName = "idx_test";
        var indexNumberField = "idx_number";
        var indexBulkOperations = IntStream.range(0, 16000)
            .mapToObj(i -> IndexOperation.of(index -> index
                .id(UUID.randomUUID().toString())
                .document(Map.of(indexNumberField, i))))
            .map(IndexOperation::_toBulkOperation)
            .toList();
        elasticsearchClient.bulk(BulkRequest.of(request -> request
            .index(indexName)
            .operations(indexBulkOperations)
            .refresh(Refresh.True)));

        var searchRequest = elasticsearchSearchRequestFactory.createSearchRequest(
            request -> request.index(indexName)
                .sort(sort -> sort.field(field -> field
                    .field(indexNumberField)
                    .order(SortOrder.Asc))),
            15000, 100
        );
        var searchResponse = elasticsearchClient.search(searchRequest, Map.class);
        searchResponse.hits().hits().forEach(hit -> {
            log.info("{}={}", indexNumberField, hit.source().get(indexNumberField));
            assertTrue(hit.source().get(indexNumberField) instanceof Integer i && i >= 15000);
        });
    }

}
