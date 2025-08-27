package com.jong.msa.board.microservice.search.request;

import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.microservice.search.validate.BetweenDate;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "게시글 검색 요청")
public record PostSearchRequest(

    @Schema(description = "제목")
    String title,

    @Schema(description = "내용")
    String content,

    @Schema(description = "작성자 ID")
    UUID writerId,

    @Schema(description = "작성자 계정")
    String writerUsername,

    @Schema(description = "작성자 이름")
    String writerName,

    @Schema(description = "작성자 그룹")
    Group writerGroup,

    @Schema(description = "생성 일자 검색 기간")
    @BetweenDate(message = "생성 일자 검색 기간 중 시작 일자가 종료 일자 보다 이후 일 수 없습니다.")
    DateRange createdDate,

    @Schema(description = "수정 일자 검색 기간")
    @BetweenDate(message = "수정 일자 검색 기간 중 시작 일자가 종료 일자 보다 이후 일 수 없습니다.")
    DateRange updatedDate,

    @Schema(description = "상태")
    State state,

    @Schema(description = "정렬 조건")
    SortOrder<SortBy> sortOrder,

    @Schema(description = "조회 시작 행")
    int offset,

    @Schema(description = "조회 건수")
    int limit

) implements SearchRequest<PostSearchRequest.SortBy> {

    @Schema(name = "PostSearchRequest.SortBy", description = "게시글 정렬 기준")
    public enum SortBy { TITLE, CONTENT, WRITER_USERNAME, CREATED_DATE, UPDATED_DATE }

}
