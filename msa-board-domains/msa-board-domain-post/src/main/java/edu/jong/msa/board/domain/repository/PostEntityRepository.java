package edu.jong.msa.board.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.jong.msa.board.domain.entity.PostEntity;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, UUID> {

}
