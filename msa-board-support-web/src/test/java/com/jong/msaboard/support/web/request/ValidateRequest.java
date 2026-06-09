package com.jong.msaboard.support.web.request;

import com.jong.msaboard.support.web.validation.NullableNotBlank;
import lombok.Builder;

@Builder
public record ValidateRequest(

    @NullableNotBlank(message = "값이 비어있을 수 없습니다.")
    String property

) {}
