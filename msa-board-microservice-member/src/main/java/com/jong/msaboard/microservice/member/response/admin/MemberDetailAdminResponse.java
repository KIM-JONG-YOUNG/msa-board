package com.jong.msaboard.microservice.member.response.admin;

import com.jong.msaboard.common.type.Gender;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.common.type.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
@Schema(description = "[관리] 회원 상세 응답")
public record MemberDetailAdminResponse(

    @Schema(description = "ID")
    UUID id,

    @Schema(description = "계정")
    String username,

    @Schema(description = "이름")
    String name,

    @Schema(description = "성별")
    Gender gender,

    @Schema(description = "이메일")
    String email,

    @Schema(description = "그룹")
    Group group,

    @Schema(description = "생성 일시")
    LocalDateTime createdDateTime,

    @Schema(description = "수정 일시")
    LocalDateTime updatedDateTime,

    @Schema(description = "상태")
    Status status

) {}
