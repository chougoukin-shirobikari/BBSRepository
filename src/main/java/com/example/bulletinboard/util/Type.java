package com.example.bulletinboard.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 会員登録の際に選択できる性別を定義したクラス
 *
 */
public class Type {
	
	public static final Map<Integer, String> GENDERS;
	
	static {
		Map<Integer, String> genders = new LinkedHashMap<>();
		genders.put(0, "男性");
		genders.put(1, "女性");
		genders.put(2, "その他");
		
		GENDERS = Collections.unmodifiableMap(genders);
	}
	

}
