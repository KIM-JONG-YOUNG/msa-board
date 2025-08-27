package com.jong.msa.board.microservice.search.request;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.microservice.search.validate.BetweenDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "회원 검색 요청")
public record MemberSearchRequest(

    @Schema(description = "계정")
    String username,

    @Schema(description = "이름")
    String name,

    @Schema(description = "성별")
    Gender gender,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "그룹")
    Group group,

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

) implements SearchRequest<MemberSearchRequest.SortBy> {

    @Schema(name = "MemberSearchRequest.SortBy", description = "회원 정렬 기준")
    public enum SortBy { USERNAME, NAME, EMAIL, CREATED_DATE, UPDATED_DATE }

}
