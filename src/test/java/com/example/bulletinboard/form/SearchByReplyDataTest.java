package com.example.bulletinboard.form;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

@SpringBootTest
@Transactional
class SearchByReplyDataTest {

	@Autowired
	Validator validator;
	
	SearchByReplyData searchByReplyData = new SearchByReplyData();
	BindingResult result = new BindException(searchByReplyData, "searchByReplyData");
	
	@BeforeEach
	public void before() {
		searchByReplyData.setGenreTitle("ジャンル名");
		searchByReplyData.setThreadTitle("スレッド名");
		searchByReplyData.setPostingNo("1");
		searchByReplyData.setReplyNo("1");
	}
	
	@Test
	void ジャンルタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByReplyData.setGenreTitle("");
		//Act
		validator.validate(searchByReplyData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("genreTitle"));
		assertThat(result.getFieldError().getDefaultMessage(), is("未入力"));
	}
	
	@Test
	void スレッドタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByReplyData.setThreadTitle("");
		//Act
		validator.validate(searchByReplyData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("threadTitle"));
		assertThat(result.getFieldError().getDefaultMessage(), is("未入力"));
	}
	
	@Test
	void postingNoに半角の数字以外が入力されたときにエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByReplyData.setPostingNo("test");
		//Act
		validator.validate(searchByReplyData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("postingNo"));
		assertThat(result.getFieldError().getDefaultMessage(), is("半角の数字を入力してください"));
	}
	
	@Test
	void replyNoに半角の数字以外が入力されたときにエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByReplyData.setReplyNo("test");
		//Act
		validator.validate(searchByReplyData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("replyNo"));
		assertThat(result.getFieldError().getDefaultMessage(), is("半角の数字を入力してください"));
	}

}









