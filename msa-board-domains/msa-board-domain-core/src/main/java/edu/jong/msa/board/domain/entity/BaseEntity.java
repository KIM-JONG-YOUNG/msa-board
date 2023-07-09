package edu.jong.msa.board.domain.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import edu.jong.msa.board.common.type.DBCodeEnum.State;
import edu.jong.msa.board.domain.converter.DBCodeAttributeConverter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

	@CreatedDate
	@Column(name = "created_date_time", nullable = false)
	private LocalDateTime createdDateTime;

	@LastModifiedDate
	@Column(name = "updated_date_time", nullable = false)
	private LocalDateTime updatedDateTime;

	@Setter
	@Convert(converter = StateCodeAttributeConverter.class)
	@Column(name = "state", length = 1, nullable = false)
	private State state = State.ACTIVE;

	@Converter
	public static class StateCodeAttributeConverter extends DBCodeAttributeConverter<State, Integer> {

		public StateCodeAttributeConverter() {
			super(State.class, false);
		}
	}

}
