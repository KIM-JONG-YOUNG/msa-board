package com.jong.msaboard.microservice.member.request.admin;

import com.jong.msaboard.common.type.Group;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "[관리] 회원 그룹 수정 요청")
public record MemberGroupModifyAdminRequest(

    @Schema(description = "그룹")
    Group group

) {}
