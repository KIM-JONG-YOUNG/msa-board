package com.jong.msaboard.microservice.member.request.admin;

import com.jong.msaboard.common.type.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "[관리] 회원 상태 수정 요청")
public record MemberStatusModifyAdminRequest(

    @Schema(description = "상태")
    Status status

) {}
