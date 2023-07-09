package edu.jong.msa.board.common.type;

public interface SortEnum {

	public static enum Order {
		DESC, ASC;
	}
	
	public static enum MemberSort {
		USERNAME, NAME, EMAIL, CREATED_DATE, UPDATED_DATE;
	}
	
}
