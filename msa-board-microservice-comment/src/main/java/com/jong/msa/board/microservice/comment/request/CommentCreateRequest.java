package com.jong.msa.board.microservice.comment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "댓글 생성 요청")
public record CommentCreateRequest(

    @Schema(description = "내용")
    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    @Size(message = "내용은 300자를 초과할 수 없습니다.", max = 300)
    String content,

    @Schema(description = "게시글 ID")
    @NotNull(message = "게시글 ID는 비어있을 수 없습니다.")
    UUID postId,

    @Schema(description = "작성자 ID")
    @NotNull(message = "작성자 ID는 비어있을 수 없습니다.")
    UUID writerId,

    @Schema(description = "부모 댓글 ID")
    UUID parentId

) {}
