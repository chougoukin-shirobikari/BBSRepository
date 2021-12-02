package com.example.bulletinboard.form;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.validator.First;
import com.example.bulletinboard.validator.Second;

@SpringBootTest
@Transactional
class ThreadDataTest {

	@Autowired
	Validator validator;
	@Autowired
	NgWordRepository ngWordRepository;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ThreadRepository threadRepository;
	
	ThreadData threadData = new ThreadData();
	
	@BeforeEach
	public void before() {
		threadData.setThreadTitle("スレッドタイトル");
		NgWord ngWord = new NgWord();
		ngWord.setNgWord("死ね");
		ngWordRepository.saveAndFlush(ngWord);
		
	}
	
	@Test
	void スレッドタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		threadData.setThreadTitle("");
		//Act
		Set<ConstraintViolation<ThreadData>> violations = validator.validate(this.threadData, First.class, Second.class);
		ConstraintViolation<ThreadData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("スレッドタイトルを入力してください"));
	}
	
	@Test
	void 入力されたスレッドタイトルにNgWordが含まれていたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		threadData.setThreadTitle("死ね");
		//Act
		Set<ConstraintViolation<ThreadData>> violations = validator.validate(this.threadData, First.class, Second.class);
		ConstraintViolation<ThreadData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("NGワードが含まれています"));
	}
	
	
	@Test
	void 入力されたスレッドタイトルが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 21; i++) {
			sb.append(i + 1);
		}
		threadData.setThreadTitle(sb.toString());
		//Act
		Set<ConstraintViolation<ThreadData>> violations = validator.validate(this.threadData, First.class, Second.class);
		ConstraintViolation<ThreadData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("２０文字以内で入力してください"));
		
	}
	
	@Test
	void 入力されたスレッドタイトルが全て全角スペースで構成されていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		threadData.setThreadTitle("　");
		//Act
		Set<ConstraintViolation<ThreadData>> violations = validator.validate(this.threadData, First.class, Second.class);
		ConstraintViolation<ThreadData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("入力に全角スペースが含まれています"));
	}

}













