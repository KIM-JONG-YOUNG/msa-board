package com.jong.msa.board.microservice.comment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.core.kafka.event.KafkaSendEvent;
import com.jong.msa.board.microservice.comment.request.CommentCreateRequest;
import com.jong.msa.board.microservice.comment.response.CommentTreeResponse;
import com.jong.msa.board.microservice.comment.service.CommentCoreService;
import com.jong.msa.board.support.domain.entity.CommentEntity;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.repository.CommentEntityRepository;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import java.util.List;
import java.util.stream.IntStream;
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
@SpringBootTest(classes = CommentMicroserviceTestContext.class)
public class CommentMicroserviceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private CommentEntityRepository commentEntityRepository;

    @MockitoSpyBean
    private CommentCoreService commentCoreService;

    @MockitoSpyBean
    private KafkaSendEvent.Listener kafkaSendEventListener;

    @DynamicPropertySource
    static void init(DynamicPropertyRegistry registry) {

        int embeddedRedisServerPort = CommentMicroserviceTestContext.EMBEDDED_REDIS_SERVER_PORT;
        registry.add("spring.data.redis.host", () -> "localhost");
        registry.add("spring.data.redis.port", () -> embeddedRedisServerPort);
        registry.add("spring.data.redis.reactive", () -> false);

        int embeddedKafkaBrokerPort = CommentMicroserviceTestContext.EMBEDDED_KAFKA_BROKER_PORT;
        String producerBootstrapServers = "spring.kafka.producer.bootstrap-servers";
        String consumerBootstrapServers = "spring.kafka.consumer.bootstrap-servers";
        String kafkaBrokerUrl = "localhost:" + embeddedKafkaBrokerPort;

        registry.add(producerBootstrapServers, () -> kafkaBrokerUrl);
        registry.add(consumerBootstrapServers, () -> kafkaBrokerUrl);

        String consumerAutoOffsetReset = "spring.kafka.consumer.auto-offset-reset";

        registry.add(consumerAutoOffsetReset, () -> "earliest");
    }

    @BeforeEach
    void beforeEach() {
        reset(commentCoreService);
        reset(kafkaSendEventListener);
    }

    @Test
    void 댓글_생성_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("create-comment-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        PostEntity post = postEntityRepository.save(PostEntity.builder()
            .title("create-comment-post-title")
            .content("create-comment-post-content")
            .writer(writer)
            .build());

        CommentEntity parent = commentEntityRepository.save(CommentEntity.builder()
            .content("create-comment-parent-content")
            .writer(writer)
            .post(post)
            .build());

        CommentCreateRequest request1 = CommentCreateRequest.builder()
            .content("create-comment-content")
            .writerId(writer.getId())
            .postId(post.getId())
            .build();

        CommentCreateRequest request2 = CommentCreateRequest.builder()
            .content("create-comment-content")
            .writerId(writer.getId())
            .postId(post.getId())
            .parentId(parent.getId())
            .build();

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
            .andExpect(status().isCreated())
            .andDo(print());

        verify(commentCoreService).create(any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());

        reset(commentCoreService);
        reset(kafkaSendEventListener);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
            .andExpect(status().isCreated())
            .andDo(print());

        verify(commentCoreService).create(any());
        verify(kafkaSendEventListener, timeout(3000)).listen(any());
    }

    @Test
    void 댓글_트리_조회_테스트() throws Exception {

        MemberEntity writer = memberEntityRepository.save(MemberEntity.builder()
            .username("get-comment-tree-username")
            .password("password")
            .name("name")
            .gender(Gender.MALE)
            .email("test@example.com")
            .group(Group.ADMIN)
            .build());

        PostEntity post = postEntityRepository.save(PostEntity.builder()
            .title("get-comment-tree-post-title")
            .content("get-comment-tree-post-content")
            .writer(writer)
            .build());

        List<CommentEntity> rootComments = commentEntityRepository.saveAll(IntStream.range(0, 10)
            .mapToObj(i -> CommentEntity.builder()
                .content("get-comment-tree-content-" + i)
                .writer(writer)
                .post(post)
                .build())
            .toList());

        rootComments.forEach(parent -> {

            List<CommentEntity> children = commentEntityRepository.saveAll(IntStream.range(0, 10)
                .mapToObj(i -> CommentEntity.builder()
                    .content("get-comment-tree-content-child-" + i)
                    .writer(writer)
                    .post(post)
                    .parent(parent)
                    .build())
                .toList());

            children.forEach(child -> {
                commentEntityRepository.saveAll(IntStream.range(0, 10)
                    .mapToObj(i -> CommentEntity.builder()
                        .content("get-comment-tree-content-grand-child-" + i)
                        .writer(writer)
                        .post(post)
                        .parent(child)
                        .build())
                    .toList());
            });
        });

        MvcResult result = mockMvc.perform(get("/api/comments/tree")
                .queryParam("postId", post.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        verify(commentCoreService).getTree(any());

        String responseBody = result.getResponse().getContentAsString();
        Class<CommentTreeResponse> responseClass = CommentTreeResponse.class;
        CommentTreeResponse detailResponse = objectMapper.readValue(responseBody, responseClass);

        assertEquals(1110, detailResponse.totalCount());
        assertEquals(10, detailResponse.list().size());

        detailResponse.list().forEach(comment -> {
            assertEquals(10, comment.children().size());
            comment.children().forEach(child -> {
                assertEquals(10, child.children().size());
            });
        });

        log.info("{}", objectMapper
            .enable(SerializationFeature.INDENT_OUTPUT)
            .writeValueAsString(detailResponse.list()));
    }

}
