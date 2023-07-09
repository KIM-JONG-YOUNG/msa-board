package edu.jong.msa.board.client.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.jong.msa.board.common.BoardConstants.Patterns;
import edu.jong.msa.board.common.type.DBCodeEnum.Gender;
import edu.jong.msa.board.common.type.DBCodeEnum.Group;
import edu.jong.msa.board.common.type.DBCodeEnum.State;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberCondition extends PagingCondition {

	private String username;

	private String name;

	private Gender gender;

	private String email;

	private Group group;

	private State state;

	@JsonFormat(pattern = Patterns.DATE_FORMAT)
	private LocalDate from;

	@JsonFormat(pattern = Patterns.DATE_FORMAT)
	private LocalDate to;

}
