package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;

@SpringBootTest
@AutoConfigureMockMvc
class PostingControllerTest3 {
	
	@Autowired
	MockMvc mockMvc;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ThreadRepository threadRepository;
	@Autowired
	PostingRepository postingRepository;
	@Autowired
	ReplyRepository replyRepository;
	
	@MockBean
	AwsService awsService;
	@InjectMocks
	private PostingController postingController;
	
	Genre genre;
	Thread thread;
	Integer genreId;
	Long threadId;
	
	@BeforeEach
	@Transactional
	public void before() {
		genre = genreRepository.findByGenreTitle("genre1");
		genreId = genre.getGenreId();
		thread = threadRepository.findByThreadTitleAndGenreId("thread1", genreId);
		threadId = thread.getThreadId();
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 画像が削除されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId((Long)any());
		
		//Act and Assert
		mockMvc.perform(get("/posting/deletePostingImage/" + posting.getPostingId()).param("page", "0"))
		.andExpect(content().string(not(containsString("画像を表示する"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 画像を削除した後Postingが新しい順に表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId((Long)any());
		
		//Act and Assert
		mockMvc.perform(get("/posting/deletePostingImage/" + posting.getPostingId()).param("page", "0").param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(not(containsString("画像を表示する"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("name3", "name2", "name1")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたPostingの画像が削除されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId((Long)any());
		
		//Act and Assert
		mockMvc.perform(get("/posting/deleteSearchedPostingImage/" + posting.getPostingId()).param("page", "0").param("bySearch", "yes").param("message", "message"))
		.andExpect(content().string(not(containsString("画像を表示する"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたPostingの画像を削除した後Postingが新しい順に表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId((Long)any());
		
		//Act and Assert
		mockMvc.perform(get("/posting/deleteSearchedPostingImage/" + posting.getPostingId()).param("page", "0").param("bySearch", "yes").param("message", "message").param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(not(containsString("画像を表示する"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("name3", "name2", "name1")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
}




