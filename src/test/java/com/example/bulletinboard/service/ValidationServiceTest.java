package com.example.bulletinboard.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.ThreadRepository;


@SpringBootTest
@Transactional
class ValidationServiceTest {
	
	@Autowired
	ValidationService validationService;
	@Autowired
	NgWordRepository ngWordRepository;
	@Autowired
	ThreadRepository threadRepository;
	@Autowired
	GenreRepository genreRepository;
	

	@Test
	void stringが全角スペースだけで構成されていたらfalseを返すことを期待します() throws Exception {
		//Arrange
		boolean expected = false;
		//Act
		boolean actual = validationService.isValid("　");
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void Ngワードが含まれていたらtrueを返すことを期待します() {
		//Arrange
		boolean expected = true;
		NgWord ngWord = new NgWord();
		ngWord.setNgWord("NGWORD");
		ngWordRepository.saveAndFlush(ngWord);
		//Act
		boolean actual = validationService.isNgWord("LMNGWORDCBA");
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void すでにthreadTitleが存在すればfalseを返すことを期待します() {
		//Arrange
		boolean expected = false;
		Genre genre = new Genre();
		genre.setGenreTitle("ジャンル名");
		genreRepository.saveAndFlush(genre);
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("スレッド名");
		Thread thread = threadData.toEntity();
		thread.setGenreId(genre.getGenreId());
		thread.setNumberOfPosting(1);
		threadRepository.saveAndFlush(thread);
		Integer genreId = genre.getGenreId();
		//Act
		boolean actual = validationService.isUnique(genreId, "スレッド名");
		//assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void genreTitleが存在すればtrueを返すことを期待します() {
		//Arrange
		boolean expected = true;
		Genre genre = new Genre();
		genre.setGenreTitle("genreTitle");
		genreRepository.saveAndFlush(genre);
		//Act
		boolean actual = validationService.genreTitleExist("genreTitle");
		//Assert
		assertThat(actual, is(expected));
	}

}


















