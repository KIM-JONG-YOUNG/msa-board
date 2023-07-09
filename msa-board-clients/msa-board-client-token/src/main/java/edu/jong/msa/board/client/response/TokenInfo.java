package edu.jong.msa.board.client.response;

import java.util.UUID;

import edu.jong.msa.board.common.type.DBCodeEnum.Group;
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
public class TokenInfo {

	private UUID memberId;

	private Group memberGroup;
	
}
