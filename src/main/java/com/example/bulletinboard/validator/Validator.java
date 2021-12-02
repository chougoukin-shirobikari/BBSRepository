package com.example.bulletinboard.validator;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.repository.NgWordRepository;

import lombok.RequiredArgsConstructor;

/**
 * バリデーションで使用する処理をまとめたクラス
 *
 */
@Component
@RequiredArgsConstructor
public class Validator {
	
	private final NgWordRepository ngWordRepository;
	
	//入力された文字列が制限をオーバーしていないかチェックする
	public boolean sizeIsValid(String str, int max) {
		int size = str.codePointCount(0, str.length());
		if(size > max) {
			return false;
		}else {
			return true;
		}
	}
	
	//入力された文字列が制限の範囲内にあるかをチェックする
	public boolean sizeIsValid(String str, int min, int max) {
		int size = str.codePointCount(0, str.length());
		if(size < min || size > max) {
			return false;
		}else {
			return true;
		}
	}
	
	//入力された文字列が空でないかをチェックする
	public boolean notBlank(String str) {
		if(str != null && !str.strip().isEmpty()) {
			return true;
		}
		return false;
	}
	
	//入力された文字列にNGワードが含まれていないかチェックする
	public boolean notNgWord(String msg) {
		List<NgWord> ngWordList = ngWordRepository.findAll();
		if(ngWordList == null) {
			return true;
		}
		for(NgWord ngWord : ngWordList) {
			if(msg.contains(ngWord.getNgWord())) {
				return false; 
			}
		}
		
		return true;
	}
	
	//入力された文字列が半角英数字で構成されているかチェック
	public boolean isHalf_WidthAlphanumeric(String str) {
		String regex = "^[A-Za-z0-9]+$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		boolean result = matcher.matches();
		return result;
	}
	
	//お問い合わせで入力したusenameが実際のusernameと一致するかチェック
	public boolean notInvalidUsername(String username) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String _username = authentication.getName();
		if(username.equals(_username)) {
			System.out.println(username + 1);
			return true;
		}else {
			System.out.println(username + 2);
			return false;
		}
	}
	

}



















