package com.jong.msa.board.microservice.member.request;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "회원 생성 요청")
public record MemberCreateRequest(

    @Schema(description = "계정")
    @NotBlank(message = "계정은 비어있을 수 없습니다.")
    @Size(message = "계정은 30자를 초과할 수 없습니다.", max = 30)
    @Pattern(message = "계정이 형식에 맞지 않습니다.", regexp = "^[a-zA-Z0-9_-]+$")
    String username,

    @Schema(description = "비밀번호")
    @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
    String password,

    @Schema(description = "이름")
    @NotBlank(message = "이름은 비어있을 수 없습니다.")
    @Size(message = "이름은 30자를 초과할 수 없습니다.", max = 30)
    String name,

    @Schema(description = "성별")
    @NotNull(message = "성별은 비어있을 수 없습니다.")
    Gender gender,

    @Schema(description = "이메일")
    @NotBlank(message = "이메일은 비어있을 수 없습니다.")
    @Size(message = "이메일은 60자를 초과할 수 없습니다.", max = 60)
    @Email(message = "이메일이 형식에 맞지 않습니다.")
    String email,

    @Schema(description = "그룹")
    @NotNull(message = "그룹은 비어있을 수 없습니다.")
    Group group

) {}
