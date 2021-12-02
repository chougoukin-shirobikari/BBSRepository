package com.example.bulletinboard.util;

/**
 * Threadに投稿できるPosting,Postingに投稿できるコメントの数を定義したenumクラス
 *
 */
public enum MaxNumber {
	
	OF_POSTING(10),
	OF_REPLY(5),
	;
	
	private final int maxNumber;
	private MaxNumber(final int maxNumber) {
		this.maxNumber = maxNumber;
	}
	
	public int getMaxNumber() {
		return this.maxNumber;
	}
	

}
