package com.jong.msa.board.microservice.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "회원 비밀번호 수정 요청")
public record MemberModifyPasswordRequest(

    @Schema(description = "현재 비밀번호")
    @NotBlank(message = "현재 는 비어있을 수 없습니다.")
    String currentPassword,

    @Schema(description = "새로운 비밀번호")
    @NotBlank(message = "새로운 비밀번호는 비어있을 수 없습니다.")
    String newPassword

) {}
