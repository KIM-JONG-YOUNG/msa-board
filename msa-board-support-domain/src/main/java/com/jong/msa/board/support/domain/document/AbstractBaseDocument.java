package com.jong.msa.board.support.domain.document;

import com.jong.msa.board.common.constants.DateTimeFormats;
import com.jong.msa.board.common.enums.State;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractBaseDocument<T extends AbstractBaseDocument<T>> {

    @Id
    private UUID id;

    @Field(name = "createdDateTime", type = FieldType.Date, format = {}, pattern = DateTimeFormats.DATE_TIME_FORMAT)
    private LocalDateTime createdDateTime;

    @Field(name = "updatedDateTime", type = FieldType.Date, format = {}, pattern = DateTimeFormats.DATE_TIME_FORMAT)
    private LocalDateTime updatedDateTime;

    @Builder.Default
    @Field(name = "state", type = FieldType.Keyword)
    private State state = State.ACTIVE;

    public T setCreatedDateTime(LocalDateTime createdDateTime) {
        this.createdDateTime = createdDateTime;
        return (T) this;
    }

    public T setUpdatedDateTime(LocalDateTime updatedDateTime) {
        this.updatedDateTime = updatedDateTime;
        return (T) this;
    }

    public T setState(State state) {
        this.state = state;
        return (T) this;
    }

}
