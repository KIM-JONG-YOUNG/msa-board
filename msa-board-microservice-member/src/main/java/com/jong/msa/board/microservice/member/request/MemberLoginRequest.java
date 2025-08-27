package com.jong.msa.board.microservice.member.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "회원 로그인 요청")
public record MemberLoginRequest(

    @Schema(description = "계정")
    @NotBlank(message = "계정은 비어있을 수 없습니다.")
    @Size(message = "계정은 30자를 초과할 수 없습니다.", max = 30)
    @Pattern(message = "계정이 형식에 맞지 않습니다.", regexp = "^[a-zA-Z0-9_-]+$")
    String username,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    String password

) {}
