package com.example.bulletinboard.controller;

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
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.SearchByPostingData;
import com.example.bulletinboard.form.SearchByReplyData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ManagementControllerTest2 {

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
	
	Genre genre;
	Thread thread;
	Posting posting;
	Reply reply;
	Integer genreId;
	Long threadId;
	Long postingId;
	
	@BeforeEach
	@Transactional
	public void before() {
		genre = genreRepository.findByGenreTitle("genre1");
		genreId = genre.getGenreId();
		thread = threadRepository.findByThreadTitleAndGenreId("thread1", genreId);
		threadId = thread.getThreadId();
		posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		postingId = posting.getPostingId();
		reply = replyRepository.findByNameAndPostingIdAndThreadIdAndGenreId("name", postingId, threadId, genreId);
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void 検索条件に一致したPostingが表示されることを期待します() throws Exception {
		//Arrange
		SearchByPostingData searchByPostingData = new SearchByPostingData();
		searchByPostingData.setGenreTitle("genre1");
		searchByPostingData.setThreadTitle("thread1");
		searchByPostingData.setPostingNo("1");
		//Act and Assert
		mockMvc
		.perform(post("/searchByPosting")
		.flashAttr("searchByPostingData", searchByPostingData).flashAttr("postingNoBySearch", posting.getPostingNo()).with(csrf()))
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void 検索条件に一致したReplyが表示されることを期待します() throws Exception {
		//Arrange
		SearchByReplyData searchByReplyData = new SearchByReplyData();
		searchByReplyData.setGenreTitle("genre1");
		searchByReplyData.setThreadTitle("thread1");
		searchByReplyData.setPostingNo("1");
		searchByReplyData.setReplyNo("1");
		//Act and Assert
		mockMvc
		.perform(post("/searchByReply")
		.flashAttr("searchByReplyData", searchByReplyData).flashAttr("replyNoBySearch", reply.getReplyNo()).with(csrf()))
		.andExpect(redirectedUrl("/reply/showReply/" + posting.getPostingId()));
		
	}

}
















