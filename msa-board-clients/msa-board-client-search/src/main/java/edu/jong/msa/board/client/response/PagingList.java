package edu.jong.msa.board.client.response;

import java.util.List;

import edu.jong.msa.board.client.request.PagingCondition;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PagingList<T> {

	private List<T> list;
	
	private int page;
	
	private int pageRows;
	
	private int count;
	
	private long totalCount;
	
	private int pageGroup;
	
	private int startPage;
	
	private int endPage;
	
	private int totalPage;
	
	public PagingList(List<T> list, PagingCondition cond, long totalCount) {
		
		this.list = list;
		this.page = cond.getPage();
		this.pageRows = cond.getPageRows();
		this.count = list.size();
		this.totalCount = totalCount;

		this.totalPage = (int) Math.ceil((double) totalCount / cond.getPageRows());
		this.pageGroup = (int) Math.ceil((double) cond.getPage() / this.totalPage);
		
		this.startPage = ((this.pageGroup - 1) * cond.getPageGroupSize()) + 1;
		this.endPage = this.pageGroup * cond.getPageGroupSize();
		
		this.startPage = (this.startPage > 0) ? this.startPage : 0;
		this.endPage = (this.endPage > this.totalPage) ? this.totalPage : this.endPage;
		
	}
	
}
