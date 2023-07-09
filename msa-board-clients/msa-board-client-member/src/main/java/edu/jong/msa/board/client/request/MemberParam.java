package edu.jong.msa.board.client.request;

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
public class MemberParam {

	private String username;
	
	private String password;
	
	private String name;
	
	private Gender gender;
	
	private String email;
	
	private Group group;
	
	private State state;

}
