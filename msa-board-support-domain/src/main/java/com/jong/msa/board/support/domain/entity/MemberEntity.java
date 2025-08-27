package com.jong.msa.board.support.domain.entity;

import com.jong.msa.board.common.enums.Gender;
import com.jong.msa.board.common.enums.Group;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Entity
@Getter
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "tb_member",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_username", columnNames = "member_username")
    },
    indexes = {
        @Index(name = "idx_member_created_date_time", columnList = "created_date_time"),
        @Index(name = "idx_member_updated_date_time", columnList = "updated_date_time")
    })
public class MemberEntity extends AbstractBaseEntity<MemberEntity> {

    @Column(name = "member_username", length = 30, nullable = false)
    private String username;

    @Setter
    @Column(name = "member_password", length = 60, nullable = false)
    private String password;

    @Setter
    @Column(name = "member_name", length = 30, nullable = false)
    private String name;

    @Setter
    @Column(name = "member_gender", length = 1, nullable = false)
    private Gender gender;

    @Setter
    @Column(name = "member_email", length = 60, nullable = false)
    private String email;

    @Setter
    @Column(name = "member_group", length = 1, nullable = false)
    private Group group;

}
