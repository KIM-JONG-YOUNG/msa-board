package com.jong.msa.board.microservice.search.feign;

import com.jong.msa.board.common.constants.MicroserviceNames;
import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = MicroserviceNames.MEMBER_MICROSERVICE)
public interface MemberFeignClient {

    @GetMapping(
        value = "/api/members/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    GetResponse get(
        @PathVariable UUID id);

    @Builder
    record GetResponse(
        UUID id,
        String username,
        String name,
        Gender gender,
        String email,
        Group group,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime,
        State state
    ) {}

}
