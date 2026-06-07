package com.jong.msaboard.support.web.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jong.msaboard.common.constants.DateTimePatterns;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.With;

@With
@Builder
@Schema(description = "오류 응답")
public record ErrorResponse(

    @Schema(description = "접근 URL")
    String path,

    @Schema(description = "상태 코드")
    Integer status,

    @Schema(description = "오류 코드")
    String code,

    @Schema(description = "오류 메세지")
    String message,

    @Schema(description = "오류 상세 목록")
    List<Detail> errors,

    @Schema(description = "오류 발생시간")
    @JsonFormat(pattern = DateTimePatterns.DATE_TIME)
    LocalDateTime timestamp

) {

    public ErrorResponse {
        timestamp = LocalDateTime.now();
    }

    @Builder
    @Schema(description = "유효성 체크 오류")
    public record Detail(

        @Schema(description = "유효성 체크 필드")
        String field,

        @Schema(description = "유효성 체크 메세지")
        String message

    ) {}

}
