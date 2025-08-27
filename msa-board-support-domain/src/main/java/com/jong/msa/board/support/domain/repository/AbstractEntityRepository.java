package com.jong.msa.board.support.domain.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractEntityRepository<T> extends JpaRepository<T, UUID> {}
