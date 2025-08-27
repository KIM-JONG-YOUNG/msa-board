package com.jong.msa.board.support.domain.service;

import com.jong.msa.board.support.domain.annotation.DistributeTransactional;
import com.jong.msa.board.support.domain.repository.PostEntityRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DistributeTransactionService {

    private final PostEntityRepository postEntityRepository;

    @DistributeTransactional(name = "test::lock", key = "#id")
    public void increasePostViews(UUID id) {
        postEntityRepository.findById(id)
            .orElseThrow(EntityNotFoundException::new)
            .increaseView();
    }


}
