package edu.jong.msa.board.client.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import edu.jong.msa.board.common.BoardConstants.Patterns;
import edu.jong.msa.board.common.type.DBCodeEnum.Gender;
import edu.jong.msa.board.common.type.DBCodeEnum.Group;
import edu.jong.msa.board.common.type.DBCodeEnum.State;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberInfo {

	private UUID id;

	private String username;
	
	private String name;
	
	private Gender gender;
	
	private String email;
	
	private Group group;
	
	@JsonFormat(pattern = Patterns.DATE_TIME_FORMAT)
	private LocalDateTime createdDateTime;
	
	@JsonFormat(pattern = Patterns.DATE_TIME_FORMAT)
	private LocalDateTime updatedDateTime;

	private State state;
	
}
