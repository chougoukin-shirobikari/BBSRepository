package com.example.bulletinboard.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.repository.UserInfoRepository;
import com.example.bulletinboard.validator.Validator;

import lombok.RequiredArgsConstructor;

/**
 * バリデーションに関するサービスクラス
 *
 */
@Service
@RequiredArgsConstructor
public class ValidationService {
	
	private final NgWordRepository ngWordRepository;
	private final GenreRepository genreRepository;
	private final ThreadRepository threadRepository;
	private final UserInfoRepository userInfoRepository;
	private final Validator validator;
	
	//nameをバリデーション
	public void nameIsValid(String name, int max, BindingResult result) {
		if(!validator.notBlank(name)) {
			FieldError fieldError = new FieldError(result.getObjectName(), "name", "名前を入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.sizeIsValid(name, max)){
			FieldError fieldError = new FieldError(result.getObjectName(), "name", max + "文字以内で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.notNgWord(name)) {
			FieldError fieldError = new FieldError(result.getObjectName(), "name", "NGワードが含まれています");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//messageをバリデーション
	public void messageIsValid(String message, String field, int max, BindingResult result) {
		if(!validator.notBlank(message)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "メッセージを入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.sizeIsValid(message, max)){
			FieldError fieldError = new FieldError(result.getObjectName(), field, max + "文字以内で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.notNgWord(message)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "NGワードが含まれています");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//usernameをバリデーション
	public void usernameIsValid(String username, String field, int min, int max, BindingResult result) {
		if(!validator.notBlank(username)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "ユーザー名を入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.sizeIsValid(username, min, max)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, min + "文字以上" + max + "文字以内で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.isHalf_WidthAlphanumeric(username)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "半角英数字で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.usernameIsUnique(username)){
			FieldError fieldError = new FieldError(result.getObjectName(), field, "入力されたユーザー名は既に登録されています");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//passwordをバリデーション
	public void passwordIsValid(String password, String field, int min, int max, BindingResult result) {
		if(!validator.notBlank(password)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "パスワードを入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.sizeIsValid(password, min, max)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, min + "文字以上で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.isHalf_WidthAlphanumeric(password)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "半角英数字で入力してください");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//お問い合わせファームに入力されたusernameのバリデーション
	public void usernameInFormIsValid(String username, String field, int max, BindingResult result) {
		if(!validator.notBlank(username)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "ユーザー名を入力してください");
			result.addError(fieldError);
		}else if(!validator.sizeIsValid(username, max)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, max + "文字以内で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.notNgWord(username)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "NGワードが含まれています");
			result.addError(fieldError);
			return;
		}else if(!validator.notInvalidUsername(username)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "ログインしたユーザー名を入力してください");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//お問い合わせフォームに入力されたmessageのバリデーション
	public void messageInFormIsValid(String message, String field, int max, BindingResult result) {
		if(!validator.notBlank(message)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "メッセージを入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.sizeIsValid(message, max)){
			FieldError fieldError = new FieldError(result.getObjectName(), field, max + "文字以内で入力してください");
			result.addError(fieldError);
			return;
		}else if(!validator.notNgWord(message)) {
			FieldError fieldError = new FieldError(result.getObjectName(), field, "NGワードが含まれています");
			result.addError(fieldError);
			return;
		}else {
			return;
		}
	}
	
	//文字列が全角スペースだけで構成されていないかチェック
	public boolean isValid(String str) {
		boolean ans = true;
		//stringが全角スペースだけで構成されていたらエラー
		if(str != null && !str.equals("")) {
			boolean isAllDoubleSpace = true;
			
			for(int i = 0; i < str.length(); i++) {
				if(str.charAt(i) != '　') {
					isAllDoubleSpace = false;
					break;
				}
			}
			
			if(isAllDoubleSpace) {
				ans = false;
			}
		}
		
		return ans;
	}
	
	//同じgenre内でthreadTitleの重複がないかチェック
	public boolean isUnique(Integer genreId, String threadTitle) {
		boolean ans = true;
		//threadTitleでthreadを取得
		List<Thread> threadList = new ArrayList<>();
		threadList = threadRepository.findByThreadTitle(threadTitle);
		//threadを取得できなければ重複無し
		if(threadList.size() == 0) {
			return ans;
		}
		//同じgenre内でthreadTitleの重複がないかをgenre_idでチェック
		for(int i = 0; i < threadList.size(); i++) {
			Integer _genreId = threadList.get(i).getGenreId();
			
			if(genreId.equals(_genreId)) {
				ans = false;
				return ans;
			}
		}
		
		return ans;
	}
	
	//Ngワードが含まれていないかチェック
	public boolean isNgWord(String msg) {
		List<NgWord> ngWordList = ngWordRepository.findAll();
		if(ngWordList == null) {
			return false;
		}
		boolean ans = false;
		for(NgWord ngWord : ngWordList) {
			if(msg.contains(ngWord.getNgWord())) {
				ans = true; 
			}
		}
		
		return ans;
	}
	
	//genre_titleが存在するかチェック
	public boolean genreTitleExist(String string) {
		Genre genre = genreRepository.findByGenreTitle(string);
		if(genre == null) {
			return false;
		}else {
			return true;
		}
	}
	
	//usernameが存在するかをチェック
	public boolean usernameExist(String username) {
		UserInfo userInfo = userInfoRepository.findByUsername(username);
		if(userInfo == null) {
			return false;
		}else {
			return true;
		}
	}
	

}



















