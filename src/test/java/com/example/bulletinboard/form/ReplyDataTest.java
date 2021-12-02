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

import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.service.ValidationService;

@SpringBootTest
@Transactional
class ReplyDataTest {
	
	@Autowired
	Validator validator;
	@Autowired
	ReplyRepository replyRepository;
	@Autowired
	ValidationService validationService;
	
	ReplyData replyData = new ReplyData();
	BindingResult result = new BindException(replyData, "ReplyData");
	
	@Test
	void nameに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		replyData.setName("");
		//Act
		validationService.nameIsValid("", 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("name"));
		assertThat(result.getFieldError().getDefaultMessage(), is("名前を入力してください"));
	}
	
	@Test
	void 入力されたnameが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 14; i++) {
			sb.append(i + 1);
		}
		replyData.setName(sb.toString());
		//Act
		validationService.nameIsValid(sb.toString(), 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("name"));
		assertThat(result.getFieldError().getDefaultMessage(), is("12文字以内で入力してください"));
	}
	
	void 入力されたnameにNgWordが含まれていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		replyData.setName("ngword");
		//Act
		validationService.nameIsValid("ngword", 12, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("name"));
		assertThat(result.getFieldError().getDefaultMessage(), is("NGワードが含まれています"));
	}
	
	@Test
	void replyMessageに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		replyData.setReplyMessage("");
		//Act
		validationService.messageIsValid("", "replyMessage", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("replyMessage"));
		assertThat(result.getFieldError().getDefaultMessage(), is("メッセージを入力してください"));
	}
	
	void 入力されたreplyMessageにNgWordが含まれていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		replyData.setReplyMessage("ngword");
		//Act
		validationService.messageInFormIsValid("ngword", "replyMessage", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("replyMessage"));
		assertThat(result.getFieldError().getDefaultMessage(), is("NGワードが含まれています"));
	}
	
	@Test
	void 入力されたreplyMessageが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 62; i++) {
			sb.append(i + 1);
		}
		replyData.setReplyMessage(sb.toString());
		//Act
		validationService.messageInFormIsValid(sb.toString(), "replyMessage", 60, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("replyMessage"));
		assertThat(result.getFieldError().getDefaultMessage(), is("60文字以内で入力してください"));
	}

}













