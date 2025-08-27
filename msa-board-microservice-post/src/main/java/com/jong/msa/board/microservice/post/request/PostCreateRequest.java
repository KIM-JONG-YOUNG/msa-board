package com.jong.msa.board.microservice.post.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "게시글 생성 요청")
public record PostCreateRequest(

    @Schema(description = "제목")
    @NotBlank(message = "제목은 비어있을 수 없습니다.")
    @Size(message = "제목은 300자를 초과할 수 없습니다.", max = 300)
    String title,

    @Schema(description = "내용")
    @NotBlank(message = "내용은 비어있을 수 없습니다.")
    @Size(message = "내용은 5000자를 초과할 수 없습니다.", max = 5000)
    String content,

    @Schema(description = "작성자 ID")
    @NotNull(message = "작성자 ID는 비어있을 수 없습니다.")
    UUID writerId

) {}
