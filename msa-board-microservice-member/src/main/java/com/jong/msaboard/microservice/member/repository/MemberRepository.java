package com.jong.msaboard.microservice.member.repository;

import com.jong.msaboard.microservice.member.entity.MemberEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, UUID>, MemberQueryRepository {}
