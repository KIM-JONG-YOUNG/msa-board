package com.jong.msa.board.microservice.search.response;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "회원 목록 응답")
public record MemberListResponse(

    @Schema(description = "총 회원 개수")
    long totalCount,

    @Schema(description = "회원 목록")
    List<Item> list

) implements SearchResponse<MemberListResponse.Item> {

    @Builder
    @Schema(name = "MemberListResponse.Item", description = "회원 목록 항목")
    public record Item(

        @Schema(description = "ID")
        UUID id,

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

        @Schema(description = "생성일시")
        LocalDateTime createdDateTime,

        @Schema(description = "수정일시")
        LocalDateTime updatedDateTime,

        @Schema(description = "상태")
        State state

    ) {}

}
