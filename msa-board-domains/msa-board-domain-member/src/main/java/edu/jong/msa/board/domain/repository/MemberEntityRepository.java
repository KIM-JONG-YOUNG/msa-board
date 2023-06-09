package edu.jong.msa.board.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.jong.msa.board.domain.entity.MemberEntity;

@Repository
public interface MemberEntityRepository extends JpaRepository<MemberEntity, UUID> {

	boolean existsByUsername(String username);

	Optional<MemberEntity> findByUsername(String username);
	
}
