package com.jong.msa.board.support.domain.repository;

import com.jong.msa.board.support.domain.entity.MemberEntity;
import java.util.Optional;

public interface MemberEntityRepository extends AbstractEntityRepository<MemberEntity> {

    boolean existsByUsername(String username);

    Optional<MemberEntity> findByUsername(String username);

}
