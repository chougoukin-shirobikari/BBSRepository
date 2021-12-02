package com.example.bulletinboard.form;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.transaction.Transactional;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;

import com.example.bulletinboard.repository.UserInfoRepository;
import com.example.bulletinboard.service.ValidationService;

@SpringBootTest
@Transactional
class UserInfoDataTest {
	
	@Autowired
	Validator validator;
	@Autowired
	UserInfoRepository userInfoRepository;
	@Autowired
	ValidationService validationService;
	
	UserInfoData userInfoData = new UserInfoData();
	BindingResult result = new BindException(userInfoData, "UserInfoData");
	
	@Test
	void usernameに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		userInfoData.setUsername("");
		//Act
		validationService.usernameIsValid("", "username", 2, 20, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("username"));
		assertThat(result.getFieldError().getDefaultMessage(), is("ユーザー名を入力してください"));
	}
	
	@Test
	void 入力されたusernameが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 20; i++) {
			sb.append(i + 1);
		}
		userInfoData.setUsername(sb.toString());
		//Act
		validationService.usernameIsValid(sb.toString(), "username", 2, 20, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("username"));
		assertThat(result.getFieldError().getDefaultMessage(), is("2文字以上20文字以内で入力してください"));
	}
	
	@Test
	void passwordに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		userInfoData.setPassword("");
		//Act
		validationService.passwordIsValid("", "password", 4, 255, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("password"));
		assertThat(result.getFieldError().getDefaultMessage(), is("パスワードを入力してください"));
	}
	
	@Test
	void 入力されたpasswordが4文字未満だった時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		userInfoData.setPassword("aa");
		//Act
		validationService.passwordIsValid("aa", "password", 4, 255, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("password"));
		assertThat(result.getFieldError().getDefaultMessage(), is("4文字以上で入力してください"));
	}
	
	@Test
	void 入力されたpasswordが半角英数字で構成されていなかったときにエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		userInfoData.setPassword("パスワード");
		//Act
		validationService.passwordIsValid("パスワード", "password", 4, 255, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("password"));
		assertThat(result.getFieldError().getDefaultMessage(), is("半角英数字で入力してください"));
	}

}













