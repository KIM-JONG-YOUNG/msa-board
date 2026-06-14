package com.jong.msaboard.microservice.member.repository;

import com.jong.msaboard.common.type.Status;
import com.jong.msaboard.microservice.member.entity.MemberEntity;
import com.jong.msaboard.microservice.member.entity.QMemberEntity;
import com.jong.msaboard.microservice.member.error.MemberErrorCode;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

public interface MemberQueryRepository {

    boolean existsByUsername(String username);

    MemberEntity findByIdOrThrow(UUID id);

    MemberEntity findByIdAndActiveOrThrow(UUID id);

    MemberEntity findByUsernameOrThrow(String username);

    MemberEntity findByUsernameAndActiveOrThrow(String username);

    @RequiredArgsConstructor
    class MemberQueryRepositoryImpl implements MemberQueryRepository {

        private final JPAQueryFactory jpaQueryFactory;

        @Override
        public boolean existsByUsername(String username) {
            return jpaQueryFactory.selectOne()
                .from(QMemberEntity.memberEntity)
                .where(QMemberEntity.memberEntity.username.eq(username))
                .fetchOne() != null;
        }

        @Override
        public MemberEntity findByIdOrThrow(UUID id) {
            var member = jpaQueryFactory
                .selectFrom(QMemberEntity.memberEntity)
                .where(QMemberEntity.memberEntity.id.eq(id))
                .fetchOne();
            if (member == null) {
                throw MemberErrorCode.NOT_FOUND_MEMBER_USERNAME.toException();
            }
            return member;
        }

        @Override
        public MemberEntity findByIdAndActiveOrThrow(UUID id) {
            var member = findByIdOrThrow(id);
            if (member.getStatus() != Status.ACTIVE) {
                throw MemberErrorCode.NOT_ACTIVE_MEMBER.toException();
            }
            return member;
        }

        @Override
        public MemberEntity findByUsernameOrThrow(String username) {
            var member = jpaQueryFactory
                .selectFrom(QMemberEntity.memberEntity)
                .where(QMemberEntity.memberEntity.username.eq(username))
                .fetchOne();
            if (member == null) {
                throw MemberErrorCode.NOT_FOUND_MEMBER_USERNAME.toException();
            }
            return member;
        }

        @Override
        public MemberEntity findByUsernameAndActiveOrThrow(String username) {
            var member = findByUsernameOrThrow(username);
            if (member.getStatus() != Status.ACTIVE) {
                throw MemberErrorCode.NOT_ACTIVE_MEMBER_USERNAME.toException();
            }
            return member;
        }
    }

}
