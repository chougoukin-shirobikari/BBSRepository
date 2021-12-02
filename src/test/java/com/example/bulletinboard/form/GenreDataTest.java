package com.example.bulletinboard.form;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.validator.All;
import com.example.bulletinboard.validator.First;
import com.example.bulletinboard.validator.Second;

@SpringBootTest
@Transactional
class GenreDataTest {

	@Autowired
	Validator validator;
	@Autowired
	NgWordRepository ngWordRepository;
	@Autowired
	GenreRepository genreRepository;
	
	GenreData genreData = new GenreData();
	
	@Test
	void ジャンルタイトルに空の値が入力されたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		genreData.setGenreTitle("");
		//Act
		Set<ConstraintViolation<GenreData>> violations = validator.validate(this.genreData, First.class, Second.class);
		ConstraintViolation<GenreData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("ジャンル名を入力してください"));
	}
	
	@Test
	void 入力されたジャンルタイトルにNgWordが含まれていたときエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		NgWord ngWord = new NgWord();
		ngWord.setNgWord("死ね");
		ngWordRepository.saveAndFlush(ngWord);
		genreData.setGenreTitle("死ね");
		//Act
		Set<ConstraintViolation<GenreData>> violations = validator.validate(genreData, All.class);
		ConstraintViolation<GenreData> violation = violations.iterator().next();
		//Assert
		
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("NGワードが含まれています"));
	}
	
	@Test
	void 入力されたジャンルタイトルが既に存在していたときにエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		Genre genre = new Genre();
		genre.setGenreTitle("ジャンル名");
		genreRepository.saveAndFlush(genre);
		genreData.setGenreTitle("ジャンル名");
		//Act
		Set<ConstraintViolation<GenreData>> violations = validator.validate(genreData, All.class);
		ConstraintViolation<GenreData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("エラー:ジャンルタイトルの重複"));
		
	}
	
	@Test
	void 入力されたジャンルタイトルが文字数制限をオーバーしていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 9; i++) {
			sb.append(i + 1);
		}
		genreData.setGenreTitle(sb.toString());
		//Act
		Set<ConstraintViolation<GenreData>> violations = validator.validate(genreData, All.class);
		ConstraintViolation<GenreData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("８文字以内で入力してください"));
	}
	
	@Test
	void 入力されたジャンルタイトルが全て全角スペースで構成されていた時にエラーメッセージが表示されることを期待します() throws Exception {
		//Arrange
		genreData.setGenreTitle("　");
		//Act
		Set<ConstraintViolation<GenreData>> violations = validator.validate(this.genreData, First.class, Second.class);
		ConstraintViolation<GenreData> violation = violations.iterator().next();
		//Assert
		assertThat(violations.size(), is(1));
		assertThat(violation.getMessage(), is("入力に全角スペースが含まれています"));
	}
	

}














