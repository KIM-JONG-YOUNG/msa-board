package com.jong.msaboard.microservice.member.request;

import com.jong.msaboard.common.type.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "[공통] 회원 가입 요청")
public record MemberJoinRequest(

    @Schema(description = "계정")
    @NotBlank(message = "계정은 필수 입력 항목입니다.")
    @Size(max = 30, message = "계정은 30자 이하여야 합니다.")
    String username,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password,

    @Schema(description = "이름")
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 30, message = "이름은 30자 이하여야 합니다.")
    String name,

    @Schema(description = "성별")
    @NotNull(message = "성별은 필수 입력 항목입니다.")
    Gender gender,

    @Schema(description = "이메일")
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 60, message = "이메일은 60자 이하여야 합니다.")
    String email

) {}
