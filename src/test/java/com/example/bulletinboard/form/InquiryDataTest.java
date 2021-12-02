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

import com.example.bulletinboard.repository.InquiryRepository;
import com.example.bulletinboard.service.ValidationService;

@SpringBootTest
@Transactional
class InquiryDataTest {
	
	@Autowired
	Validator validator;
	@Autowired
	InquiryRepository inquiryRepository;
	@Autowired
	ValidationService validationService;
	
	InquiryData inquiryData = new InquiryData();
	BindingResult result = new BindException(inquiryData, "InquiryData");
	
	@Test
	void usernameに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		inquiryData.setUsername("");
		//Act
		validationService.usernameInFormIsValid("", "username", 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("username"));
		assertThat(result.getFieldError().getDefaultMessage(), is("ユーザー名を入力してください"));
	}
	
	@Test
	void 入力されたusernameが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 14; i++) {
			sb.append(i + 1);
		}
		inquiryData.setUsername(sb.toString());
		//Act
		validationService.usernameInFormIsValid(sb.toString(), "username", 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("username"));
		assertThat(result.getFieldError().getDefaultMessage(), is("12文字以内で入力してください"));
	}
	
	void 入力されたusernameにNgWordが含まれていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		inquiryData.setUsername("ngword");
		//Act
		validationService.usernameInFormIsValid("ngword", "username", 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("username"));
		assertThat(result.getFieldError().getDefaultMessage(), is("NGワードが含まれています"));
	}
	
	@Test
	void messageに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		inquiryData.setMessage("");
		//Act
		validationService.messageInFormIsValid("", "message", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("message"));
		assertThat(result.getFieldError().getDefaultMessage(), is("メッセージを入力してください"));
	}
	
	void 入力されたmessageにNgWordが含まれていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		inquiryData.setMessage("ngword");
		//Act
		validationService.messageInFormIsValid("ngword", "message", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("message"));
		assertThat(result.getFieldError().getDefaultMessage(), is("NGワードが含まれています"));
	}
	
	@Test
	void 入力されたmessageが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 62; i++) {
			sb.append(i + 1);
		}
		inquiryData.setMessage(sb.toString());
		//Act
		validationService.messageInFormIsValid(sb.toString(), "message", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("message"));
		assertThat(result.getFieldError().getDefaultMessage(), is("60文字以内で入力してください"));
	}

}













