package com.jong.msa.board.microservice.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.jong.msa.board.microservice.post.request.PostCreateRequest;
import com.jong.msa.board.microservice.post.request.PostModifyRequest;
import com.jong.msa.board.microservice.post.response.PostDetailsResponse;
import com.jong.msa.board.microservice.post.service.PostCoreService;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = PostMicroserviceTestContext.class)
public class PostMicroserviceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @Autowired
    private PostEntityRepository postEntityRepository;

    @MockitoSpyBean
    private PostCoreService postCoreService;

    @MockitoSpyBean
    private KafkaSendEvent.Listener kafkaSendEventListener;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {

        int embeddedRedisServerPort = PostMicroserviceTestContext.EMBEDDED_REDIS_SERVER_PORT;
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> embeddedRedisServerPort);
        registry.add("spring.data.redis.reactive", () -> false);

        String producerBootstrapServers = "spring.kafka.producer.bootstrap-servers";
        String consumerBootstrapServers = "spring.kafka.consumer.bootstrap-servers";
        String kafkaBrokerUrl = "localhost:" + PostMicroserviceTestContext.EMBEDDED_KAFKA_BROKER_PORT;

        registry.add(producerBootstrapServers, () -> kafkaBrokerUrl);
        registry.add(consumerBootstrapServers, () -> kafkaBrokerUrl);

        String consumerAutoOffsetReset = "spring.kafka.consumer.auto-offset-reset";

        registry.add(consumerAutoOffsetReset, () -> "earliest");
    }

    @BeforeEach
    void beforeEach() {
        reset(postCoreService);
        reset(kafkaSendEventListener);
    }

    @Test
    void 게시글_생성_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("create-post-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        PostCreateRequest request = PostCreateRequest.builder()
            .title("create-post-title")
            .content("create-post-content")
            .writerId(writer.getId())
            .build();

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andDo(print());

        verify(postCoreService).create(any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());
    }

    @Test
    void 게시글_수정_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("modify-post-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        UUID id = postEntityRepository.save(PostEntity.builder()
                .title("modify-post-title")
                .content("modify-post-content")
                .writer(writer)
                .build())
            .getId();

        PostModifyRequest request = PostModifyRequest.builder()
            .title("modified-post-title")
            .content("modified-post-content")
            .state(State.INACTIVE)
            .build();

        mockMvc.perform(put("/api/posts/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNoContent())
            .andDo(print());

        verify(postCoreService).modify(any(), any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());

        PostEntity entity = postEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);

        assertEquals(entity.getTitle(), request.title());
        assertEquals(entity.getContent(), request.content());
        assertEquals(entity.getState(), request.state());
    }

    @Test
    void 게시글_조회수_증가_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("increase-post-views-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        UUID id = postEntityRepository.save(PostEntity.builder()
                .title("increase-post-views-title")
                .content("increase-post-views-content")
                .writer(writer)
                .build())
            .getId();

        mockMvc.perform(patch("/api/posts/" + id + "/views/increase"))
            .andExpect(status().isNoContent())
            .andDo(print());

        verify(postCoreService).increaseViews(any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());

        PostEntity entity = postEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);

        assertEquals(entity.getViews(), 1);
    }

    @Test
    void 게시글_조회_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("get-post-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        PostEntity entity = postEntityRepository.save(PostEntity.builder()
            .title("get-post-title")
            .content("get-post-content")
            .writer(writer)
            .build());

        MvcResult result = mockMvc.perform(get("/api/posts/" + entity.getId())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        verify(postCoreService).get(any());

        String responseBody = result.getResponse().getContentAsString();
        Class<PostDetailsResponse> responseClass = PostDetailsResponse.class;
        PostDetailsResponse detailResponse = objectMapper.readValue(responseBody, responseClass);

        assertEquals(detailResponse.id(), entity.getId());
        assertEquals(detailResponse.title(), entity.getTitle());
        assertEquals(detailResponse.content(), entity.getContent());
        assertEquals(detailResponse.views(), entity.getViews());
        assertEquals(detailResponse.state(), entity.getState());
    }

}
