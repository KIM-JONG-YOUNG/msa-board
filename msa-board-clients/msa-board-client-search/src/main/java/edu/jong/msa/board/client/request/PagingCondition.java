package edu.jong.msa.board.client.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class PagingCondition {

	@Builder.Default
	private int page = 1;

	@Builder.Default
	private int pageRows = 10;
	
	@Builder.Default
	private int pageGroupSize = 3;

	public int ofOffset() {
		int offset = (getPage() - 1) * getPageRows();
		return (offset > 0) ? offset : 0;
	}
	
}
