package com.jong.msa.board.microservice.post.request;

import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.support.web.validate.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "게시글 수정 요청")
public record PostModifyRequest(

    @Schema(description = "제목")
    @NullableNotBlank(message = "제목은 비어있을 수 없습니다.")
    @Size(message = "제목은 300자를 초과할 수 없습니다.", max = 300)
    String title,

    @Schema(description = "내용")
    @NullableNotBlank(message = "내용은 비어있을 수 없습니다.")
    @Size(message = "내용은 5000자를 초과할 수 없습니다.", max = 5000)
    String content,

    @Schema(description = "상태")
    State state

) {}
