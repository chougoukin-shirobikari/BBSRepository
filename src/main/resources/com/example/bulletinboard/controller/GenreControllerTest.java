package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.form.GenreData;
import com.example.bulletinboard.repository.GenreRepository;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	GenreRepository genreRepository;
	
	Genre genre = new Genre();
	
	@BeforeEach
	@Transactional
	public void before() {
		genre.setGenreTitle("ジャンル名");
		genreRepository.saveAndFlush(genre);
	}

	@Test
	@Transactional
	void Getリクエストが実行されたときGenreを表示することを期待します() throws Exception{
		mockMvc
		.perform(get("/genre/showGenre"))
		.andExpect(view().name("Genre"));
	}
	
	@Test
	@Transactional
	void Getリクエストが実行されたときGenreFormを表示することを期待します() throws Exception{
		mockMvc
		.perform(get("/genre/toGenreForm"))
		.andExpect(view().name("GenreForm"));
	}
	
	@Test
	@Transactional
	void ジャンルを登録するとき成功することを期待します() throws Exception {
		GenreData genreData = new GenreData();
		genreData.setGenreTitle("genre");
		mockMvc.perform(post("/genre/create").flashAttr("genreData", genreData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/genre/showGenre"));
	}
	
	@Test
	@Transactional
	void ジャンルを選択したときスレッドを表示することを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(view().name("Thread"));
	}

	@Test
	@Transactional
	void 削除するを選択したときジャンルが削除されることを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/genre/delete/" + genreId))
		.andExpect(content().string(not(containsString("ジャンル名"))))
		.andExpect(redirectedUrl("/genre/showGenre"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "user", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します() throws Exception {
		//Act and Assert
		mockMvc
		.perform(get("/genre/showGenre"))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Genre"));
	}

}












