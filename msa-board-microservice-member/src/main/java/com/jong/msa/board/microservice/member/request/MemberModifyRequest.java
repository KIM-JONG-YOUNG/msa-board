package com.jong.msa.board.microservice.member.request;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.support.web.validate.NullableNotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "회원 수정 요청")
public record MemberModifyRequest(

    @Schema(description = "이름")
    @NullableNotBlank(message = "이름은 비어있을 수 없습니다.")
    @Size(message = "이름은 30자를 초과할 수 없습니다.", max = 30)
    String name,

    @Schema(description = "성별")
    Gender gender,

    @Schema(description = "이메일")
    @NullableNotBlank(message = "이메일은 비어있을 수 없습니다.")
    @Size(message = "이메일은 60자를 초과할 수 없습니다.", max = 60)
    @Email(message = "이메일이 형식에 맞지 않습니다.")
    String email,

    @Schema(description = "그룹")
    Group group,

    @Schema(description = "상태")
    State state

) {}
