package com.jong.msa.board.microservice.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.microservice.member.request.MemberCreateRequest;
import com.jong.msa.board.microservice.member.request.MemberLoginRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyPasswordRequest;
import com.jong.msa.board.microservice.member.request.MemberModifyRequest;
import com.jong.msa.board.microservice.member.response.MemberDetailsResponse;
import com.jong.msa.board.microservice.member.service.MemberCoreService;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = MemberMicroserviceTestContext.class)
public class MemberMicroserviceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @MockitoSpyBean
    private MemberCoreService memberCoreService;

    @MockitoSpyBean
    private KafkaSendEvent.Listener kafkaSendEventListener;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {

        int embeddedRedisServerPort = MemberMicroserviceTestContext.EMBEDDED_REDIS_SERVER_PORT;
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> embeddedRedisServerPort);
        registry.add("spring.data.redis.reactive", () -> false);

        String producerBootstrapServers = "spring.kafka.producer.bootstrap-servers";
        String consumerBootstrapServers = "spring.kafka.consumer.bootstrap-servers";
        String kafkaBrokerUrl = "localhost:" + MemberMicroserviceTestContext.EMBEDDED_KAFKA_BROKER_PORT;

        registry.add(producerBootstrapServers, () -> kafkaBrokerUrl);
        registry.add(consumerBootstrapServers, () -> kafkaBrokerUrl);

        String consumerAutoOffsetReset = "spring.kafka.consumer.auto-offset-reset";

        registry.add(consumerAutoOffsetReset, () -> "earliest");
    }

    @BeforeEach
    void beforeEach() {
        reset(memberCoreService);
        reset(kafkaSendEventListener);
    }

    @Test
    void 회원_생성_테스트() throws Exception {

        MemberCreateRequest request = MemberCreateRequest.builder()
            .username("create-member-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build();

        mockMvc.perform(post("/api/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print());

        verify(memberCoreService).create(any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());
    }

    @Test
    void 회원_수정_테스트() throws Exception {

        UUID id = memberEntityRepository.save(MemberEntity.builder()
                .username("modify-member-username")
                .password("password")
                .name("modify-name")
                .gender(Gender.MALE)
                .email("modify-test@example.com")
                .group(Group.ADMIN)
                .build())
            .getId();

        MemberModifyRequest request = MemberModifyRequest.builder()
            .name("modified-member-name")
            .gender(Gender.FEMALE)
            .email("modified-member-test@example.com")
            .group(Group.USER)
            .state(State.INACTIVE)
            .build();

        mockMvc.perform(put("/api/members/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent())
            .andDo(print());

        verify(memberCoreService).modify(any(), any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());

        MemberEntity entity = memberEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);

        assertEquals(entity.getName(), request.name());
        assertEquals(entity.getGender(), request.gender());
        assertEquals(entity.getGroup(), request.group());
        assertEquals(entity.getState(), request.state());
    }

    @Test
    void 회원_비밀번호_수정_테스트() throws Exception {

        UUID id = memberEntityRepository.save(MemberEntity.builder()
                .username("modify-password-username")
                .password(passwordEncoder.encode("password"))
                .name("name")
                .gender(Gender.MALE)
                .email("test@example.com")
                .group(Group.ADMIN)
                .build())
            .getId();

        MemberModifyPasswordRequest request = MemberModifyPasswordRequest.builder()
            .currentPassword("password")
            .newPassword("new-password")
            .build();

        mockMvc.perform(patch("/api/members/" + id + "/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent())
            .andDo(print());

        verify(memberCoreService).modifyPassword(any(), any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());

        MemberEntity entity = memberEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);

        assertTrue(passwordEncoder.matches(request.newPassword(), entity.getPassword()));
    }

    @Test
    void 회원_조회_테스트() throws Exception {

        MemberEntity entity = memberEntityRepository.save(MemberEntity.builder()
            .username("get-member-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        MvcResult result = mockMvc.perform(get("/api/members/" + entity.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        verify(memberCoreService).get(any());

        String responseBody = result.getResponse().getContentAsString();
        MemberDetailsResponse detailsResponse = objectMapper.readValue(responseBody, MemberDetailsResponse.class);

        assertEquals(detailsResponse.id(), entity.getId());
        assertEquals(detailsResponse.username(), entity.getUsername());
        assertEquals(detailsResponse.name(), entity.getName());
        assertEquals(detailsResponse.gender(), entity.getGender());
        assertEquals(detailsResponse.email(), entity.getEmail());
        assertEquals(detailsResponse.state(), entity.getState());
    }

    @Test
    void 회원_로그인_테스트() throws Exception {

        String username = "login-member-username";
        String password = "password";
        MemberEntity entity = memberEntityRepository.save(MemberEntity.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        MemberLoginRequest request = MemberLoginRequest.builder()
            .username(username)
            .password(password)
            .build();

        MvcResult result = mockMvc.perform(post("/api/members/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        verify(memberCoreService).login(any());

        String responseBody = result.getResponse().getContentAsString();
        MemberDetailsResponse detailsResponse = objectMapper.readValue(responseBody, MemberDetailsResponse.class);

        assertEquals(detailsResponse.id(), entity.getId());
        assertEquals(detailsResponse.username(), entity.getUsername());
        assertEquals(detailsResponse.name(), entity.getName());
        assertEquals(detailsResponse.gender(), entity.getGender());
        assertEquals(detailsResponse.email(), entity.getEmail());
        assertEquals(detailsResponse.state(), entity.getState());
    }

}
