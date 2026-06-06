package com.jong.msaboard.support.infra.factory;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.FieldAndFormat;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest.Builder;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.util.ApiTypeHelper;
import com.jong.msaboard.support.infra.condition.ConditionalOnElasticsearch;
import java.util.function.UnaryOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnElasticsearch
public class ElasticsearchRequestFactory {

    private static final int MAX_SEARCH_COUNT = 10000;

    private final ElasticsearchClient elasticsearchClient;

    public SearchRequest createSearchRequest(UnaryOperator<Builder> operator, int offset, int limit) {
        try {

            var loopCount = offset / MAX_SEARCH_COUNT;
            var loopCountRemainder = offset % MAX_SEARCH_COUNT;

            var searchAfter = ApiTypeHelper.<FieldValue>undefinedList();
            var sourceConfigFalse = SourceConfig.of(source -> source.fetch(false));
            var emptyReturnFields = ApiTypeHelper.<FieldAndFormat>undefinedList();

            for (int i = 0; i < loopCount; i++) {

                var searchRequest = operator.apply(new Builder())
                    .size(MAX_SEARCH_COUNT)
                    .searchAfter(searchAfter)
                    .source(sourceConfigFalse)
                    .fields(emptyReturnFields)
                    .build();

                var searchResponse = elasticsearchClient.search(searchRequest);
                var searchResponseHits = searchResponse.hits().hits();
                if (searchResponseHits.isEmpty()) {
                    throw new RuntimeException("Elasticsearch Document 개수가 offset 보다 작습니다.");
                }

                searchAfter = searchResponseHits.getLast().sort();
            }

            if (loopCountRemainder > 0) {

                var searchRequest = operator.apply(new Builder())
                    .size(loopCountRemainder)
                    .searchAfter(searchAfter)
                    .source(sourceConfigFalse)
                    .fields(emptyReturnFields)
                    .build();

                var searchResponse = elasticsearchClient.search(searchRequest);
                var searchResponseHits = searchResponse.hits().hits();
                if (searchResponseHits.isEmpty()) {
                    throw new RuntimeException("Elasticsearch Document 개수가 offset 보다 작습니다.");
                }

                searchAfter = searchResponseHits.getLast().sort();
            }

            return operator.apply(new Builder()).size(limit).searchAfter(searchAfter).build();

        } catch (Exception e) {
            log.error("Elasticsearch Search After를 계산하는데 오류가 발생했습니다.", e);
            throw new RuntimeException("Elasticsearch Search After를 계산하는데 오류가 발생했습니다.", e);
        }
    }

}
