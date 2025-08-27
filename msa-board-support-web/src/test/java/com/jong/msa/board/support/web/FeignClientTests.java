package com.jong.msa.board.support.web;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.jsonResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.patch;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.jong.msa.board.support.web.feign.FeignTestClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Slf4j
@SpringBootTest(
    classes = WebTestContext.class,
    properties = {
        "spring.main.web-application-type=servlet",
        "spring.cloud.openfeign.okhttp.enabled=true",
        "logging.level.com.jong.msa.board.support.web.feign=debug",
    })
public class FeignClientTests {

    @Autowired
    private FeignTestClient feignTestClient;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {

        String feignClientName = FeignTestClient.FEIGN_CLIENT_NAME;
        String feignClientPrefix = "spring.cloud.openfeign.client.config." + feignClientName;
        String feignClientURL = "http://localhost:" + WebTestContext.WIRE_MOCK_SERVER_PORT;

        registry.add(feignClientPrefix + ".url", () -> feignClientURL);
        registry.add(feignClientPrefix + ".logger-level", () -> "full");
        registry.add(feignClientPrefix + ".connect-timeout", () -> 30000);
        registry.add(feignClientPrefix + ".read-timeout", () -> 30000);
    }

    @Test
    void FeignClient_테스트() {

        WireMockServer wireMockServer = WebTestContext.WIRE_MOCK_SERVER;
        wireMockServer.stubFor(get(urlPathEqualTo("/api/get"))
            .willReturn(jsonResponse(new FeignTestClient.Response("get-result-body"), 200)));
        wireMockServer.stubFor(post(urlPathEqualTo("/api/post"))
            .willReturn(jsonResponse(new FeignTestClient.Response("post-result-body"), 200)));
        wireMockServer.stubFor(put(urlPathEqualTo("/api/put"))
            .willReturn(jsonResponse(new FeignTestClient.Response("put-result-body"), 200)));
        wireMockServer.stubFor(patch(urlPathEqualTo("/api/patch"))
            .willReturn(jsonResponse(new FeignTestClient.Response("patch-result-body"), 200)));
        wireMockServer.stubFor(delete(urlPathEqualTo("/api/delete"))
            .willReturn(jsonResponse(new FeignTestClient.Response("delete-result-body"), 200)));

        feignTestClient.get("get-param-value");
        feignTestClient.post(new FeignTestClient.Request("post-body-value"));
        feignTestClient.put(new FeignTestClient.Request("post-body-value"));
        feignTestClient.patch(new FeignTestClient.Request("post-body-value"));
        feignTestClient.delete("delete-param-value");
    }

}
