package com.jong.msaboard.microservice.member.request;

import com.jong.msaboard.common.type.Gender;
import com.jong.msaboard.support.web.validation.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "[공통] 회원 정보 수정 요청")
public record MemberModifyRequest(

    @Schema(description = "이름")
    @NullableNotBlank(message = "이름은 공백일 수 없습니다.")
    String name,

    @Schema(description = "성별")
    Gender gender,

    @Schema(description = "이메일")
    @NullableNotBlank(message = "이메일은 공백일 수 없습니다.")
    @Size(max = 60, message = "이메일은 60자 이하여야 합니다.")
    String email

) {}
