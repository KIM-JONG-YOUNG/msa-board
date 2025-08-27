package com.jong.msa.board.support.web.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = FeignTestClient.FEIGN_CLIENT_NAME)
public interface FeignTestClient {

    String FEIGN_CLIENT_NAME = "external-application";

    @GetMapping(
        value = "/api/get",
        produces = MediaType.APPLICATION_JSON_VALUE)
    Response get(
        @RequestParam String param
    );

    @PostMapping(
        value = "/api/post",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    Response post(
        @RequestBody Request request
    );

    @PutMapping(
        value = "/api/put",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    Response put(
        @RequestBody Request request
    );

    @PatchMapping(
        value = "/api/patch",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    Response patch(
        @RequestBody Request request
    );

    @DeleteMapping(
        value = "/api/delete",
        produces = MediaType.APPLICATION_JSON_VALUE)
    Response delete(
        @RequestParam String param
    );

    record Request(String param) {}

    record Response(String result) {}

}
