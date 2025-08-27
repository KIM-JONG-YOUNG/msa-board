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
@Schema(description = "댓글 목록 응답")
public record CommentListResponse(

    @Schema(description = "총 댓글 개수")
    long totalCount,

    @Schema(description = "댓글 목록")
    List<Item> list

) implements SearchResponse<CommentListResponse.Item> {

    @Builder
    @Schema(name = "CommentListResponse.Item", description = "댓글 목록 항목")
    public record Item(

        @Schema(description = "ID")
        UUID id,

        @Schema(description = "내용")
        String content,

        @Schema(description = "작성자")
        Writer writer,

        @Schema(description = "게시글")
        Post post,

        @Schema(description = "부모 댓글")
        Parent parent,

        @Schema(description = "생성 일시")
        LocalDateTime createdDateTime,

        @Schema(description = "수정 일시")
        LocalDateTime updatedDateTime,

        @Schema(description = "상태")
        State state

    ) {}

    @Builder
    @Schema(name = "CommentListResponse.Writer", description = "댓글 작성자")
    public record Writer(

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

        @Schema(description = "상태")
        State state

    ) {}

    @Builder
    @Schema(name = "CommentListResponse.Post", description = "댓글 게시글")
    public record Post(

        @Schema(description = "ID")
        UUID id,

        @Schema(description = "제목")
        String title,

        @Schema(description = "상태")
        State state

    ) {}

    @Builder
    @Schema(name = "CommentListResponse.Parent", description = "부모 댓글")
    public record Parent(

        @Schema(description = "ID")
        UUID id,

        @Schema(description = "내용")
        String content,

        @Schema(description = "상태")
        State state

    ) {}


}
