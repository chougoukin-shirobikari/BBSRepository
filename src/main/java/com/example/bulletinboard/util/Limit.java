package com.example.bulletinboard.util;

/**
 * 1ページ当たりに表示するデータの数を定義したenumクラス
 */

public enum Limit {
	
	POSTING_LIMIT(5),
	THREAD_LIMIT(5),
	USERINFO_LIMIT(3),
	INQUIRY_LIMIT(3),
	;
	
	private final int limit;
	private Limit(final int limit) {
		this.limit = limit;
	}
	
	public int getLimit() {
		return this.limit;
	}
	

}
