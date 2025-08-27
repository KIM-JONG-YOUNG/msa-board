package com.jong.msa.board.microservice.search.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.DateRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.CountRequest;
import co.elastic.clients.elasticsearch.core.CountResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.util.ApiTypeHelper;
import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.microservice.search.mapper.CommentDocumentMapper;
import com.jong.msa.board.microservice.search.mapper.MemberDocumentMapper;
import com.jong.msa.board.microservice.search.mapper.PostDocumentMapper;
import com.jong.msa.board.microservice.search.request.CommentSearchRequest;
import com.jong.msa.board.microservice.search.request.MemberSearchRequest;
import com.jong.msa.board.microservice.search.request.PostSearchRequest;
import com.jong.msa.board.microservice.search.request.SearchRequest.DateRange;
import com.jong.msa.board.microservice.search.response.CommentListResponse;
import com.jong.msa.board.microservice.search.response.MemberListResponse;
import com.jong.msa.board.microservice.search.response.PostListResponse;
import com.jong.msa.board.support.domain.document.CommentDocument;
import com.jong.msa.board.support.domain.document.MemberDocument;
import com.jong.msa.board.support.domain.document.PostDocument;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchCoreService {

    private final ElasticsearchClient elasticsearchClient;

    private final MemberDocumentMapper memberDocumentMapper;

    private final PostDocumentMapper postDocumentMapper;

    private final CommentDocumentMapper commentDocumentMapper;

    private BoolQuery buildStringMatchQuery(String field, String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return BoolQuery.of(b -> b
            .should(s -> s.match(m -> m.field(field + ".bigram").query(value)))
            .should(s -> s.match(m -> m.field(field + ".morph").query(value)))
        );
    }

    private TermQuery buildTermQuery(String field, Enum value) {
        if (value == null) {
            return null;
        }
        return buildTermQuery(field, value.name());
    }

    private TermQuery buildTermQuery(String field, String value) {
        if (value == null) {
            return null;
        }
        return TermQuery.of(t -> t.field(field).value(value));
    }

    private RangeQuery buildDateRangeQuery(String field, DateRange dateRange) {

        if (dateRange == null) {
            return null;
        }

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (dateRange.from() != null) {
            fromDateTime = LocalDateTime.of(dateRange.from(), LocalTime.of(0, 0, 0));
        }
        if (dateRange.to() != null) {
            toDateTime = LocalDateTime.of(dateRange.to(), LocalTime.of(23, 59, 59));
        }

        return new RangeQuery.Builder()
            .date(new DateRangeQuery.Builder()
                .field(field)
                .format(DateTimeFormats.DATE_TIME_FORMAT)
                .gte(fromDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER))
                .lte(toDateTime.format(DateTimeFormats.DATE_TIME_FORMATTER))
                .build())
            .build();
    }

    private SearchRequest buildSearchRequest(
        Supplier<SearchRequest.Builder> requestSupplier, long offset, long limit
    ) throws Exception {

        SearchRequest searchRequest = null;
        SearchResponse<?> searchResponse = null;

        List<FieldValue> cursor = ApiTypeHelper.undefinedList();

        int loopBatchSize = 10000;
        int loopCount = (int) (offset / loopBatchSize);
        int loopAfterSearchSize = (int) (offset % loopBatchSize);

        for (int i = 0; i < loopCount; i++) {
            searchRequest = requestSupplier.get().searchAfter(cursor).size(i * loopBatchSize).build();
            searchResponse = elasticsearchClient.search(searchRequest);
            cursor = searchResponse.hits().hits().getLast().sort();
        }

        if (loopAfterSearchSize > 0) {
            searchRequest = requestSupplier.get().searchAfter(cursor).size(loopAfterSearchSize).build();
            searchResponse = elasticsearchClient.search(searchRequest);
            cursor = searchResponse.hits().hits().getLast().sort();
        }

        return requestSupplier.get().searchAfter(cursor).size((int) limit).build();
    }

    public MemberListResponse search(MemberSearchRequest request) throws Exception {

        BoolQuery usernameQuery = buildStringMatchQuery("username", request.username());
        BoolQuery nameQuery = buildStringMatchQuery("name", request.name());
        BoolQuery emailQuery = buildStringMatchQuery("email", request.email());

        TermQuery genderQuery = buildTermQuery("gender", request.gender());
        TermQuery groupQuery = buildTermQuery("group", request.group());
        TermQuery stateQuery = buildTermQuery("state", request.state());

        RangeQuery createdDateQuery = buildDateRangeQuery("createdDateTime", request.createdDate());
        RangeQuery updatedDateQuery = buildDateRangeQuery("updatedDateTime", request.updatedDate());

        BoolQuery searchQuery = new BoolQuery.Builder()
            .filter(genderQuery)
            .filter(groupQuery)
            .filter(stateQuery)
            .filter(createdDateQuery)
            .filter(updatedDateQuery)
            .must(usernameQuery)
            .must(nameQuery)
            .must(emailQuery)
            .build();

        SortOptions sortOption = request.sortOrder() == null
            ? SortOptions.of(s -> s.field(f -> f.field("_score").order(SortOrder.Desc)))
            : SortOptions.of(s -> s.field(f -> f
                .field(switch (request.sortOrder().sortBy()) {
                    case USERNAME -> "username";
                    case NAME -> "name";
                    case EMAIL -> "email";
                    case CREATED_DATE -> "createdDateTime";
                    case UPDATED_DATE -> "updatedDateTime";
                    case null -> "_score";
                })
                .order(switch (request.sortOrder().orderBy()) {
                    case ASC -> SortOrder.Asc;
                    case DESC -> SortOrder.Desc;
                    case null -> switch (request.sortOrder().sortBy()) {
                        case USERNAME -> SortOrder.Asc;
                        case NAME -> SortOrder.Asc;
                        case EMAIL -> SortOrder.Asc;
                        case CREATED_DATE -> SortOrder.Desc;
                        case UPDATED_DATE -> SortOrder.Desc;
                        case null -> SortOrder.Desc;
                    };
                })));

        CountRequest countRequest = CountRequest.of(c -> c.index("members").query(searchQuery));
        CountResponse countResponse = elasticsearchClient.count(countRequest);

        if (countResponse.count() == 0 || countResponse.count() < request.offset()) {
            return MemberListResponse.builder()
                .totalCount(countResponse.count())
                .build();
        }

        Supplier<SearchRequest.Builder> searchRequestSupplier = () -> new SearchRequest.Builder()
            .index("members")
            .query(searchQuery)
            .sort(sortOption);

        SearchRequest searchRequest = buildSearchRequest(searchRequestSupplier, request.offset(), request.limit());
        SearchResponse<MemberDocument> searchResponse = elasticsearchClient.search(searchRequest, MemberDocument.class);

        return MemberListResponse.builder()
            .totalCount(countResponse.count())
            .list(searchResponse.hits().hits().stream()
                .map(Hit::source)
                .map(memberDocumentMapper::toListItem)
                .toList())
            .build();
    }

    public PostListResponse search(PostSearchRequest request) throws Exception {

        BoolQuery titleQuery = buildStringMatchQuery("title", request.title());
        BoolQuery contentQuery = buildStringMatchQuery("content", request.content());
        BoolQuery writerUsernameQuery = buildStringMatchQuery("writer.username", request.writerUsername());
        BoolQuery writerNameQuery = buildStringMatchQuery("writer.name", request.writerName());

        TermQuery writerIdQuery = buildTermQuery("writer.id", request.writerId().toString());
        TermQuery writerGroupQuery = buildTermQuery("writer.group", request.writerGroup());
        TermQuery stateQuery = buildTermQuery("state", request.state());

        RangeQuery createdDateQuery = buildDateRangeQuery("createdDateTime", request.createdDate());
        RangeQuery updatedDateQuery = buildDateRangeQuery("updatedDateTime", request.updatedDate());

        BoolQuery searchQuery = new BoolQuery.Builder()
            .filter(writerIdQuery)
            .filter(writerGroupQuery)
            .filter(stateQuery)
            .filter(createdDateQuery)
            .filter(updatedDateQuery)
            .must(titleQuery)
            .must(contentQuery)
            .must(writerUsernameQuery)
            .must(writerNameQuery)
            .build();

        SortOptions sortOption = request.sortOrder() == null
            ? SortOptions.of(s -> s.field(f -> f.field("_score").order(SortOrder.Desc)))
            : SortOptions.of(s -> s.field(f -> f
                .field(switch (request.sortOrder().sortBy()) {
                    case TITLE -> "title";
                    case CONTENT -> "content";
                    case WRITER_USERNAME -> "writer.username";
                    case CREATED_DATE -> "createdDateTime";
                    case UPDATED_DATE -> "updatedDateTime";
                    case null -> "_score";
                })
                .order(switch (request.sortOrder().orderBy()) {
                    case ASC -> SortOrder.Asc;
                    case DESC -> SortOrder.Desc;
                    case null -> switch (request.sortOrder().sortBy()) {
                        case TITLE -> SortOrder.Asc;
                        case CONTENT -> SortOrder.Asc;
                        case WRITER_USERNAME -> SortOrder.Asc;
                        case CREATED_DATE -> SortOrder.Desc;
                        case UPDATED_DATE -> SortOrder.Desc;
                        case null -> SortOrder.Desc;
                    };
                })));

        CountRequest countRequest = CountRequest.of(c -> c.index("posts").query(searchQuery));
        CountResponse countResponse = elasticsearchClient.count(countRequest);
        if (countResponse.count() == 0 || countResponse.count() < request.offset()) {
            return PostListResponse.builder()
                .totalCount(countResponse.count())
                .build();
        }

        Supplier<SearchRequest.Builder> searchRequestSupplier = () -> new SearchRequest.Builder()
            .index("posts")
            .query(searchQuery)
            .sort(sortOption);

        SearchRequest searchRequest = buildSearchRequest(searchRequestSupplier, request.offset(), request.limit());
        SearchResponse<PostDocument> searchResponse = elasticsearchClient.search(searchRequest, PostDocument.class);

        return PostListResponse.builder()
            .totalCount(countResponse.count())
            .list(searchResponse.hits().hits().stream()
                .map(Hit::source)
                .map(postDocumentMapper::toListItem)
                .toList())
            .build();
    }

    public CommentListResponse search(CommentSearchRequest request) throws Exception {

        BoolQuery contentQuery = buildStringMatchQuery("content", request.content());
        BoolQuery writerUsernameQuery = buildStringMatchQuery("writer.username", request.writerUsername());
        BoolQuery writerNameQuery = buildStringMatchQuery("writer.name", request.writerName());

        TermQuery postIdQuery = buildTermQuery("post.id", request.postId().toString());
        TermQuery parentIdQuery = buildTermQuery("parent.id", request.parentId().toString());
        TermQuery writerIdQuery = buildTermQuery("writer.id", request.writerId().toString());
        TermQuery writerGroupQuery = buildTermQuery("writer.group", request.writerGroup());
        TermQuery stateQuery = buildTermQuery("state", request.state());

        RangeQuery createdDateQuery = buildDateRangeQuery("createdDateTime", request.createdDate());
        RangeQuery updatedDateQuery = buildDateRangeQuery("updatedDateTime", request.updatedDate());

        BoolQuery searchQuery = new BoolQuery.Builder()
            .filter(postIdQuery)
            .filter(parentIdQuery)
            .filter(writerIdQuery)
            .filter(writerGroupQuery)
            .filter(stateQuery)
            .filter(createdDateQuery)
            .filter(updatedDateQuery)
            .must(contentQuery)
            .must(writerUsernameQuery)
            .must(writerNameQuery)
            .build();

        SortOptions sortOption = request.sortOrder() == null
            ? SortOptions.of(s -> s.field(f -> f.field("_score").order(SortOrder.Desc)))
            : SortOptions.of(s -> s.field(f -> f
                .field(switch (request.sortOrder().sortBy()) {
                    case CONTENT -> "content";
                    case WRITER_USERNAME -> "writer.username";
                    case CREATED_DATE -> "createdDateTime";
                    case UPDATED_DATE -> "updatedDateTime";
                    case null -> "_score";
                })
                .order(switch (request.sortOrder().orderBy()) {
                    case ASC -> SortOrder.Asc;
                    case DESC -> SortOrder.Desc;
                    case null -> switch (request.sortOrder().sortBy()) {
                        case CONTENT -> SortOrder.Asc;
                        case WRITER_USERNAME -> SortOrder.Asc;
                        case CREATED_DATE -> SortOrder.Desc;
                        case UPDATED_DATE -> SortOrder.Desc;
                        case null -> SortOrder.Desc;
                    };
                })));

        CountRequest countRequest = CountRequest.of(c -> c.index("comments").query(searchQuery));
        CountResponse countResponse = elasticsearchClient.count(countRequest);
        if (countResponse.count() == 0 || countResponse.count() < request.offset()) {
            return CommentListResponse.builder()
                .totalCount(countResponse.count())
                .build();
        }

        Supplier<SearchRequest.Builder> searchRequestSupplier = () -> new SearchRequest.Builder()
            .index("comments")
            .query(searchQuery)
            .sort(sortOption);

        SearchRequest searchRequest = buildSearchRequest(searchRequestSupplier, request.offset(), request.limit());
        SearchResponse<CommentDocument> searchResponse = elasticsearchClient.search(searchRequest, CommentDocument.class);

        return CommentListResponse.builder()
            .totalCount(countResponse.count())
            .list(searchResponse.hits().hits().stream()
                .map(Hit::source)
                .map(commentDocumentMapper::toListItem)
                .toList())
            .build();
    }

}
