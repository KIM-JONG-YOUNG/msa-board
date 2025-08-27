package com.jong.msa.board.microservice.search.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import lombok.Builder;

public interface SearchRequest<E extends Enum<E>> {

    SortOrder<E> sortOrder();

    DateRange createdDate();

    DateRange updatedDate();

    @Schema(description = "정렬 방식")
    enum OrderBy { ASC, DESC }

    @Schema(description = "정렬 조건")
    record SortOrder<E extends Enum<E>>(

        @Schema(description = "기준")
        E sortBy,

        @Schema(description = "방식")
        OrderBy orderBy

    ) {}

    @Builder
    @Schema(description = "검색 기간")
    record DateRange(

        @Schema(description = "시작 일자")
        LocalDate from,

        @Schema(description = "종료 일자")
        LocalDate to

    ) {}


}
