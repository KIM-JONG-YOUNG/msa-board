package com.jong.msa.board.support.domain.entity;

import com.jong.msa.board.common.enums.State;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractBaseEntity<T extends AbstractBaseEntity<T>> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @CreatedDate
    @Column(name = "created_date_time", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    @Column(name = "updated_date_time", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime updatedDateTime;

    @Column(name = "state", length = 1, nullable = false)
    private State state = State.ACTIVE;

    public T setState(State state) {
        this.state = state;
        return (T) this;
    }

}
