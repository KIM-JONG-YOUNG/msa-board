package com.jong.msa.board.microservice.post.response;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "게시글 상세 응답")
public record PostDetailsResponse(

    @Schema(description = "ID")
    UUID id,

    @Schema(description = "제목")
    String title,

    @Schema(description = "내용")
    String content,

    @Schema(description = "작성자")
    Writer writer,

    @Schema(description = "조회수")
    int views,

    @Schema(description = "생성 일시")
    LocalDateTime createdDateTime,

    @Schema(description = "수정 일시")
    LocalDateTime updatedDateTime,

    @Schema(description = "상태")
    State state

) {

    @Builder
    @Schema(name = "PostDetailsResponse.Writer", description = "게시글 작성자")
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
