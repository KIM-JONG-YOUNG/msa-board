package com.jong.msaboard.microservice.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msaboard.common.constants.HeaderNames;
import com.jong.msaboard.common.constants.KafkaTopicNames;
import com.jong.msaboard.common.type.Gender;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.common.type.Status;
import com.jong.msaboard.microservice.member.entity.MemberEntity;
import com.jong.msaboard.microservice.member.error.MemberErrorCode;
import com.jong.msaboard.microservice.member.repository.MemberRepository;
import com.jong.msaboard.microservice.member.request.MemberJoinRequest;
import com.jong.msaboard.microservice.member.request.MemberModifyRequest;
import com.jong.msaboard.microservice.member.request.MemberPasswordModifyRequest;
import com.jong.msaboard.support.test.factory.EmbeddedRedisServerFactory;
import com.jong.msaboard.support.web.exception.ErrorCodeException;
import com.jong.msaboard.support.web.service.TokenMvcService;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.RedisServer;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(Lifecycle.PER_CLASS)
@EmbeddedKafka(
    partitions = 1, ports = 0,
    topics = KafkaTopicNames.MEMBER_SAVE
)
public class MemberApiTest {

    static final RedisServer REDIS_SERVER = EmbeddedRedisServerFactory.createEmbeddedRedisServer();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TokenMvcService tokenMvcService;

    UUID testAdminId;
    UUID testUserId;

    String testAdminAccessToken;
    String testUserAccessToken;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {
        REDIS_SERVER.start();
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> REDIS_SERVER.ports().getFirst());
    }

    @BeforeAll
    void beforeAll() {

        var testAdmin = memberRepository.save(MemberEntity.builder()
            .username("test-admin")
            .password(passwordEncoder.encode("1234"))
            .name("테스트 관리자")
            .gender(Gender.MALE)
            .email("test-admin@example.com")
            .group(Group.ADMIN)
            .status(Status.ACTIVE)
            .build());

        var testUser = memberRepository.save(MemberEntity.builder()
            .username("test-user")
            .password(passwordEncoder.encode("1234"))
            .name("테스트 일반회원")
            .gender(Gender.MALE)
            .email("test-user@example.com")
            .group(Group.USER)
            .status(Status.ACTIVE)
            .build());

        testAdminId = testAdmin.getId();
        testUserId = testUser.getId();

        testAdminAccessToken = tokenMvcService.generateAccessToken(testAdmin.getId(), testAdmin.getGroup());
        testUserAccessToken = tokenMvcService.generateAccessToken(testUser.getId(), testUser.getGroup());
    }

    @BeforeEach
    void beforeEach() {

        var testAdmin = memberRepository.save(memberRepository.findByIdOrThrow(testAdminId)
            .setPassword(passwordEncoder.encode("1234")));
        var testUser = memberRepository.save(memberRepository.findByIdOrThrow(testUserId)
            .setPassword(passwordEncoder.encode("1234")));

        testAdminAccessToken = tokenMvcService.generateAccessToken(testAdmin.getId(), testAdmin.getGroup());
        testUserAccessToken = tokenMvcService.generateAccessToken(testUser.getId(), testUser.getGroup());
    }

    @AfterAll
    void afterAll() {
        REDIS_SERVER.stop();
    }

    @Test
    void 회원_가입_테스트() throws Exception {

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MemberJoinRequest.builder()
                    .username("test-user")
                    .password("1234")
                    .name("테스트 일반회원")
                    .gender(Gender.MALE)
                    .email("test-user@example.com")
                    .build())))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value(MemberErrorCode.ALREADY_EXISTS_MEMBER_USERNAME.code()));

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(MemberJoinRequest.builder()
                    .username("test-user-2")
                    .password("1234")
                    .name("테스트 일반회원")
                    .gender(Gender.MALE)
                    .email("test-user@example.com")
                    .build())))
            .andExpect(status().isCreated());
    }

    @Test
    void 현재_회원_정보_수정_테스트() throws Exception {

        mockMvc.perform(put("/api/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testUserAccessToken)
                .content(objectMapper.writeValueAsString(MemberModifyRequest.builder()
                    .name("테스트 일반회원 수정")
                    .gender(Gender.FEMALE)
                    .email("test-user@example-update.com")
                    .build())))
            .andExpect(status().isNoContent());

        var updatedUser = memberRepository.findByIdOrThrow(testUserId);
        assertEquals(updatedUser.getName(), "테스트 일반회원 수정");
        assertEquals(updatedUser.getGender(), Gender.FEMALE);
        assertEquals(updatedUser.getEmail(), "test-user@example-update.com");

        mockMvc.perform(put("/api/members/me")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testAdminAccessToken)
                .content(objectMapper.writeValueAsString(MemberModifyRequest.builder()
                    .name("테스트 관리자 수정")
                    .gender(Gender.FEMALE)
                    .email("test-admin@example-update.com")
                    .build())))
            .andExpect(status().isNoContent());

        var updatedAdmin = memberRepository.findByIdOrThrow(testAdminId);
        assertEquals(updatedAdmin.getName(), "테스트 관리자 수정");
        assertEquals(updatedAdmin.getGender(), Gender.FEMALE);
        assertEquals(updatedAdmin.getEmail(), "test-admin@example-update.com");
    }

    @Test
    void 현재_회원_비밀번호_수정_테스트() throws Exception {

        mockMvc.perform(patch("/api/members/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testUserAccessToken)
                .content(objectMapper.writeValueAsString(MemberPasswordModifyRequest.builder()
                    .currentPassword("1234")
                    .newPassword("12345")
                    .build())))
            .andExpect(status().isNoContent());

        var updatedUser = memberRepository.findByIdOrThrow(testUserId);
        assertTrue(passwordEncoder.matches("12345", updatedUser.getPassword()));

        mockMvc.perform(patch("/api/members/me/password")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testAdminAccessToken)
                .content(objectMapper.writeValueAsString(MemberPasswordModifyRequest.builder()
                    .currentPassword("1234")
                    .newPassword("12345")
                    .build())))
            .andExpect(status().isNoContent());

        var updatedAdmin = memberRepository.findByIdOrThrow(testAdminId);
        assertTrue(passwordEncoder.matches("12345", updatedAdmin.getPassword()));
    }

    @Test
    void 현재_회원_상세_조회_테스트() throws Exception {

        var testUser = memberRepository.findByIdOrThrow(testUserId);
        mockMvc.perform(get("/api/members/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testUserAccessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testUser.getId().toString()))
            .andExpect(jsonPath("$.username").value(testUser.getUsername()))
            .andExpect(jsonPath("$.name").value(testUser.getName()))
            .andExpect(jsonPath("$.gender").value(testUser.getGender().name()))
            .andExpect(jsonPath("$.email").value(testUser.getEmail()))
            .andExpect(jsonPath("$.group").value(testUser.getGroup().name()));

        var testAdmin = memberRepository.findByIdOrThrow(testAdminId);
        mockMvc.perform(get("/api/members/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testAdminAccessToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(testAdmin.getId().toString()))
            .andExpect(jsonPath("$.username").value(testAdmin.getUsername()))
            .andExpect(jsonPath("$.name").value(testAdmin.getName()))
            .andExpect(jsonPath("$.gender").value(testAdmin.getGender().name()))
            .andExpect(jsonPath("$.email").value(testAdmin.getEmail()))
            .andExpect(jsonPath("$.group").value(testAdmin.getGroup().name()));
    }

    @Test
    void 현재_회원_탈퇴_테스트() throws Exception {

        mockMvc.perform(delete("/api/members/me")
                .header(HeaderNames.ACCESS_TOKEN, testUserAccessToken))
            .andExpect(status().isNoContent());

        assertThrows(ErrorCodeException.class, () -> {
            tokenMvcService.getAuthenticationFromAccessToken(testUserAccessToken);
        });

        mockMvc.perform(delete("/api/members/me")
                .accept(MediaType.APPLICATION_JSON)
                .header(HeaderNames.ACCESS_TOKEN, testAdminAccessToken))
            .andExpect(status().isNoContent());

        assertThrows(ErrorCodeException.class, () -> {
            tokenMvcService.getAuthenticationFromAccessToken(testAdminAccessToken);
        });
    }

    @Test
    void 회원_로그인_테스트() throws Exception {
    }

    @Test
    void 회원_로그아웃_테스트() throws Exception {
    }

    @Test
    void 회원_인증_토큰_갱신_테스트() throws Exception {
    }

}
