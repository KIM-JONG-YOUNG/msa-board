package com.jong.msaboard.microservice.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "[공통] 회원 로그인 요청")
public record MemberLoginRequest(

    @Schema(description = "계정")
    @NotBlank(message = "계정은 필수 입력 항목입니다.")
    @Size(max = 30, message = "계정은 30자 이하여야 합니다.")
    String username,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password

) {}
