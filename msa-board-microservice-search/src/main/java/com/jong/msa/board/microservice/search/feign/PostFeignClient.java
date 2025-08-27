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

@FeignClient(name = MicroserviceNames.POST_MICROSERVICE)
public interface PostFeignClient {

    @GetMapping(
        value = "/api/posts/{id}",
        produces = MediaType.APPLICATION_JSON_VALUE)
    GetResponse get(
        @PathVariable UUID id);

    @Builder
    record GetResponse(
        UUID id,
        String title,
        String content,
        Writer writer,
        int views,
        LocalDateTime createdDateTime,
        LocalDateTime updatedDateTime,
        State state
    ) {}

    @Builder
    record Writer(
        UUID id,
        String username,
        String name,
        Gender gender,
        String email,
        Group group,
        State state
    ) {}

}
