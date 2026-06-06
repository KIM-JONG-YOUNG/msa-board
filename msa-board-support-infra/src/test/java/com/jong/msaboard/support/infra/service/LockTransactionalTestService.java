package com.jong.msaboard.support.infra.service;

import com.jong.msaboard.support.infra.entity.CountEntity;
import com.jong.msaboard.support.infra.repository.CountEntityRepository;
import com.jong.msaboard.support.infra.transaction.LockTransactional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockTransactionalTestService {

    private final CountEntityRepository countEntityRepository;

    @Transactional
    public UUID createTestEntity() {
        return countEntityRepository.save(CountEntity.builder().build()).id();
    }

    @LockTransactional(name = "test::lock::", key = "#id")
    public void increaseTestEntityCount(UUID id) {
        var countEntity = countEntityRepository.findById(id).orElseThrow();
        countEntityRepository.save(countEntity.count(countEntity.count() + 1));
    }

    @Transactional(readOnly = true)
    public int getTestEntityCount(UUID id) {
        return countEntityRepository.findById(id).orElseThrow().count();
    }

}
