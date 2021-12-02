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
class SearchByPostingDataTest {

	@Autowired
	Validator validator;
	
	SearchByPostingData searchByPostingData = new SearchByPostingData();
	BindingResult result = new BindException(searchByPostingData, "searchByPostingData");
	
	@BeforeEach
	public void before() {
		searchByPostingData.setGenreTitle("ジャンル名");
		searchByPostingData.setThreadTitle("スレッド名");
		searchByPostingData.setPostingNo("1");
	}
	
	@Test
	void ジャンルタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByPostingData.setGenreTitle("");
		//Act
		validator.validate(searchByPostingData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("genreTitle"));
		assertThat(result.getFieldError().getDefaultMessage(), is("未入力"));
	}
	
	@Test
	void スレッドタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByPostingData.setThreadTitle("");
		//Act
		validator.validate(searchByPostingData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("threadTitle"));
		assertThat(result.getFieldError().getDefaultMessage(), is("未入力"));
	}
	
	@Test
	void postingNoに半角の数字以外が入力されたときにエラーメッセージが表示されることを期待します() {
		//Arrange
		searchByPostingData.setPostingNo("test");
		//Act
		validator.validate(searchByPostingData, result);
		//Assert
		assertThat(result.getFieldError().getField(), is("postingNo"));
		assertThat(result.getFieldError().getDefaultMessage(), is("半角の数字を入力してください"));
	}

}









