package com.jong.msaboard.microservice.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "[공통] 회원 비밀번호 수정 요청")
public record MemberPasswordModifyRequest(

    @Schema(description = "현재 비밀번호")
    @NotBlank(message = "현재 비밀번호는 필수 입력 항목입니다.")
    String currentPassword,

    @Schema(description = "신규 비밀번호")
    @NotBlank(message = "신규 비밀번호는 필수 입력 항목입니다.")
    String newPassword

) {}
