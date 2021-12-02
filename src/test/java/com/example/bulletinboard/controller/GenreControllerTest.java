package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.form.GenreData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.service.AwsService;

@SpringBootTest
@AutoConfigureMockMvc
class GenreControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@Autowired
	GenreRepository genreRepository;
	
	@MockBean
	AwsService awsService;
	@InjectMocks
	private GenreController genreController;
	
	Genre genre;

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Getリクエストが実行されたときジャンル一覧画面を表示することを期待します() throws Exception{
		mockMvc
		.perform(get("/genre/showGenre"))
		.andExpect(view().name("Genre"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Getリクエストが実行されたときGenreForm画面を表示することを期待します() throws Exception{
		mockMvc
		.perform(get("/genre/toGenreForm"))
		.andExpect(view().name("GenreForm"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "user", roles = "USER")
	void Genreを登録するときに成功することを期待します() throws Exception {
		//Arrange
		GenreData genreData = new GenreData();
		genreData.setGenreTitle("genre2");
		//Act and Assert
		mockMvc.perform(post("/genre/create").flashAttr("genreData", genreData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/genre/showGenre"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void ジャンルを選択したときにスレッド一覧画面が表示されることを期待します() throws Exception {
		//Arrange
		genre = genreRepository.findByGenreTitle("genre1");
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(view().name("Thread"));
	}

	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 削除するを選択したときにジャンルが削除されることを期待します() throws Exception {
		//Arrange
		genre = genreRepository.findByGenreTitle("genre1");
		Integer genreId = genre.getGenreId();
		doNothing().when(awsService).deleteImageFromS3ByGenreId(genreId);
		//Act and Assert
		mockMvc
		.perform(get("/genre/delete/" + genreId))
		.andExpect(content().string(not(containsString("genre1"))))
		.andExpect(view().name("fragments/GenreFragment :: genreFragment"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void AmazonServiceExcpetionが発生したときにError画面が表示されることを期待します() throws Exception {
		//Arrange
		genre = genreRepository.findByGenreTitle("genre1");
		Integer genreId = genre.getGenreId();
		doThrow(new AmazonServiceException("例外が発生しました")).when(awsService).deleteImageFromS3ByGenreId(genreId);
		//Act and Assert
		mockMvc
		.perform(get("/genre/delete/" + genreId))
		.andExpect(view().name("error"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/genre/showGenre"))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Genre"));
	}

}












