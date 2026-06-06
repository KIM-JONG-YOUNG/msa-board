package com.jong.msaboard.support.infra.repository;

import com.jong.msaboard.support.infra.entity.CountEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountEntityRepository extends JpaRepository<CountEntity, UUID> {}
