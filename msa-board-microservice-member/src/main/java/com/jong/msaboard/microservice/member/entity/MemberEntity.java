package com.jong.msaboard.microservice.member.entity;

import com.jong.msaboard.common.type.Gender;
import com.jong.msaboard.common.type.Group;
import com.jong.msaboard.common.type.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.UuidGenerator.Style;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@Accessors(chain = true)
@Table(name = "tb_member")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity {

    @Id
    @JdbcTypeCode(SqlTypes.CHAR)
    @UuidGenerator(style = Style.TIME)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", unique = true, length = 30, nullable = false)
    private String username;

    @Setter
    @Column(name = "password", length = 60, nullable = false)
    private String password;

    @Setter
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    @Setter
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Setter
    @Column(name = "email", length = 60, nullable = false)
    private String email;

    @Setter
    @Column(name = "group", nullable = false)
    private Group group;

    @CreatedDate
    @Column(name = "created_date_time", nullable = false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "updated_date_time", nullable = false)
    private LocalDateTime updatedDateTime;

    @Setter
    @Column(name = "status", nullable = false)
    private Status status;

}
