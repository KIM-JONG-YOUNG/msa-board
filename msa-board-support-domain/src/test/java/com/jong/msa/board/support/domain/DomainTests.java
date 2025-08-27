package com.jong.msa.board.support.domain;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import com.jong.msa.board.common.enums.State;
import com.jong.msa.board.support.domain.entity.MemberEntity;
import com.jong.msa.board.support.domain.entity.PostEntity;
import com.jong.msa.board.support.domain.repository.MemberEntityRepository;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import com.jong.msa.board.support.domain.service.DistributeTransactionService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.PersistenceUnit;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = DomainTestContext.class)
public class DomainTests {

    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private MemberEntityRepository memberEntityRepository;

    @Autowired
    private PostEntityRepository postEntityRepository;

    @Autowired
    private DistributeTransactionService distributeTransactionService;

    @Test
    void contextLoads() {
        log.info("JPA contextLoads 및 Elasticsearch 인덱스 생성 성공!");
    }

    @Test
    void AttributeConverter_테스트() {

        UUID id = UUID.randomUUID();

        EntityTransaction transaction = null;
        try (EntityManager entityManager = entityManagerFactory.createEntityManager()) {

            transaction = entityManager.getTransaction();
            transaction.begin();

            entityManager.createNativeQuery("""
                    INSERT INTO `tb_member`(
                      id,
                      member_username,
                      member_password,
                      member_name,
                      member_gender,
                      member_email,
                      member_group,
                      created_date_time,
                      updated_date_time,
                      state
                    ) VALUES (
                      :id,
                      :username,
                      :password,
                      :name,
                      :gender,
                      :email,
                      :group,
                      :createdDateTime,
                      :updatedDateTime,
                      :state
                    )
                    """)
                .setParameter("id", id)
                .setParameter("username", "username")
                .setParameter("password", "password")
                .setParameter("name", "name")
                .setParameter("gender", "M")
                .setParameter("email", "email")
                .setParameter("group", 1)
                .setParameter("createdDateTime", LocalDateTime.now())
                .setParameter("updatedDateTime", LocalDateTime.now())
                .setParameter("state", "1")
                .executeUpdate();

            transaction.commit();

            MemberEntity entity = memberEntityRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);

            assertEquals(entity.getGender(), Gender.MALE);
            assertEquals(entity.getGroup(), Group.ADMIN);
            assertEquals(entity.getState(), State.ACTIVE);

        } catch (Exception e) {
            transaction.rollback();
            fail(e);
        }
    }

    @Test
    void DistributeTransactional_테스트() {

        UUID id = postEntityRepository.save(PostEntity.builder()
                .title("title")
                .content("content")
                .writer(memberEntityRepository.save(MemberEntity.builder()
                    .username("lock-username")
                    .password("password")
                    .name("name")
                    .gender(Gender.MALE)
                    .email("email")
                    .group(Group.ADMIN)
                    .build()))
                .build())
            .getId();

        Executor executor = Executors.newVirtualThreadPerTaskExecutor();
        Runnable runnable = () -> distributeTransactionService.increasePostViews(id);

        int loopCount = 1000;

        CompletableFuture.allOf(IntStream.range(0, loopCount)
                .mapToObj(i -> CompletableFuture.runAsync(runnable, executor))
                .toArray(CompletableFuture[]::new))
            .join();

        PostEntity entity = postEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new);

        assertEquals(loopCount, entity.getViews());
    }

}
