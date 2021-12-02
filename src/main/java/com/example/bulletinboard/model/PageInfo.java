package com.example.bulletinboard.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ページングに必要なパラメータを保持するクラス
 *
 */
@NoArgsConstructor
@Data
public class PageInfo {
	
	private int limit;
	private int currentpage;
	private int total;
	private int totalpage;
	private boolean isFirst;
	private boolean isLast;
	
	public PageInfo(int limit, int currentpage, int total, int totalpage, boolean isFirst, boolean isLast) {
		this.limit = limit;
		this.currentpage = currentpage;
		this.total = total;
		this.totalpage = totalpage;
		this.isFirst = isFirst;
		this.isLast = isLast;
		
	}

}
