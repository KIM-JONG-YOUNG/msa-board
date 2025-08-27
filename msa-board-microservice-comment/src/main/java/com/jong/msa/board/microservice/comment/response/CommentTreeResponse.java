package com.jong.msa.board.microservice.comment.response;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "댓글 트리 응답")
public record CommentTreeResponse(

    @Schema(description = "총 댓글 개수")
    long totalCount,

    @Schema(description = "댓글 목록")
    List<Item> list

) {

    @Builder
    @Schema(name = "CommentTreeResponse.Item", description = "댓글 트리 항목")
    public record Item(

        @Schema(description = "ID")
        UUID id,

        @Schema(description = "내용")
        String content,

        @Schema(description = "작성자 ID")
        Writer writer,

        @Schema(description = "하위 댓글 목록")
        List<Item> children,

        @Schema(description = "생성 일시")
        LocalDateTime createdDateTime,

        @Schema(description = "수정 일시")
        LocalDateTime updatedDateTime,

        @Schema(description = "상태")
        State state

    ) {}

    @Builder
    @Schema(name = "CommentTreeResponse.Item", description = "댓글 작성자")
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

}
