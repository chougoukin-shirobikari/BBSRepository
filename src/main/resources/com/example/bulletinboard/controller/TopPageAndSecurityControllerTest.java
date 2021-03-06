package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.form.ReplyData;
import com.example.bulletinboard.form.SearchByPostingData;
import com.example.bulletinboard.form.SearchByReplyData;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TopPageAndSecurityControllerTest {

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
	@Autowired
	NgWordRepository ngWordRepository;
	
	@Test
	void TopPage????????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(get("/TopPage"))
		.andExpect(view().name("TopPage"));
	}
	
	@Test
	void login????????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(get("/login"))
		.andExpect(view().name("login"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ????????????????????????Management????????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(get("/toManagement"))
		.andExpect(view().name("Management"));
	}
	
	@Test
	void ?????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(formLogin().user("user").password("password"))
		.andExpect(redirectedUrl("/toManagement"))
		.andExpect(authenticated().withRoles("ADMIN"));
	}
	
	@Test
	void ?????????????????????????????????login????????????????????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(get("/toManagement"))
		.andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ??????????????????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(post("/logout").with(csrf()))
		.andExpect(redirectedUrl("/TopPage"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ???????????????????????????Posting??????????????????????????????????????????() throws Exception {
		//Arrange
		Genre genre = new Genre();
		genre.setGenreTitle("???????????????");
		genreRepository.saveAndFlush(genre);
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("???????????????");
		Thread thread = threadData.toEntity();
		thread.setGenreId(genre.getGenreId());
		thread.setNumberOfPosting(1);
		//List<Thread> threadList = new ArrayList<>();
		//threadList.add(thread);
		//genre.setThreadList(threadList);
		threadRepository.saveAndFlush(thread);
		Long threadId = thread.getThreadId();
		
		PostingData postingData1 = new PostingData();
		postingData1.setPostingNo(0L);
		postingData1.setName("?????????1");
		postingData1.setMessage("???????????????");
		Posting posting = postingData1.toEntity();
		posting.setThreadId(thread.getThreadId());
		//List<Posting> postingList = new ArrayList<>();
		//postingList.add(posting);
		//thread.setPostingList(postingList);
		postingRepository.saveAndFlush(posting);
		
		ReplyData replyData = new ReplyData();
		replyData.setReplyNo(0);
		replyData.setName("?????????");
		replyData.setReplyMessage("???????????????");
		Reply reply = replyData.toEntity();
		reply.setPostingId(posting.getPostingId());
		//List<Reply> replyList = new ArrayList<>();
		//replyList.add(reply);
		//posting.setReplyList(replyList);
		replyRepository.saveAndFlush(reply);
		
		SearchByPostingData searchByPostingData = new SearchByPostingData();
		searchByPostingData.setGenreTitle("???????????????");
		searchByPostingData.setThreadTitle("???????????????");
		searchByPostingData.setPostingNo("1");
		//Act and Assert
		mockMvc
		.perform(post("/searchByPosting")
				.flashAttr("searchByPostingData", searchByPostingData).flashAttr("postingNoBySearch", posting.getPostingNo()).with(csrf()))
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ???????????????????????????Reply??????????????????????????????????????????() throws Exception {
		//Arrange
		Genre genre = new Genre();
		genre.setGenreTitle("???????????????");
		genreRepository.saveAndFlush(genre);
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("???????????????");
		Thread thread = threadData.toEntity();
		thread.setGenreId(genre.getGenreId());
		thread.setNumberOfPosting(1);
		//List<Thread> threadList = new ArrayList<>();
		//threadList.add(thread);
		//genre.setThreadList(threadList);
		threadRepository.saveAndFlush(thread);
		
		PostingData postingData1 = new PostingData();
		postingData1.setPostingNo(0L);
		postingData1.setName("?????????1");
		postingData1.setMessage("???????????????");
		Posting posting = postingData1.toEntity();
		posting.setThreadId(thread.getThreadId());
		//List<Posting> postingList = new ArrayList<>();
		//postingList.add(posting);
		//thread.setPostingList(postingList);
		postingRepository.saveAndFlush(posting);
		
		ReplyData replyData = new ReplyData();
		replyData.setReplyNo(0);
		replyData.setName("?????????");
		replyData.setReplyMessage("???????????????");
		Reply reply = replyData.toEntity();
		reply.setPostingId(posting.getPostingId());
		//List<Reply> replyList = new ArrayList<>();
		//replyList.add(reply);
		//posting.setReplyList(replyList);
		replyRepository.saveAndFlush(reply);
		
		SearchByReplyData searchByReplyData = new SearchByReplyData();
		searchByReplyData.setGenreTitle("???????????????");
		searchByReplyData.setThreadTitle("???????????????");
		searchByReplyData.setPostingNo("1");
		searchByReplyData.setReplyNo("1");
		//Act and Assert
		mockMvc
		.perform(post("/searchByReply")
				.flashAttr("searchByReplyData", searchByReplyData).flashAttr("replyNoBySearch", reply.getReplyNo()).with(csrf()))
		.andExpect(redirectedUrl("/reply/showReply/" + posting.getPostingId()));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void NgWord??????????????????????????????????????????() throws Exception {
		mockMvc
		.perform(post("/registerNgWord").param("ngWord", "??????").with(csrf()))
		.andExpect(redirectedUrl("/toManagement"));
		
		mockMvc
		.perform(get("/toManagement").flashAttr("registerNgWord", "1"))
		.andExpect(content().string(containsString("??????")));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void NgWord??????????????????????????????????????????() throws Exception {
		//Arrange
		NgWord ngWord = new NgWord();
		ngWord.setNgWord("??????");
		ngWordRepository.saveAndFlush(ngWord);
		Integer ngWordId = ngWord.getNgWordId();
		//Act and Assert
		mockMvc
		.perform(get("/deleteNgWord/" + ngWordId))
		.andExpect(redirectedUrl("/toManagement"));
		
		mockMvc
		.perform(get("/toManagement").flashAttr("registerNgWord", "1"))
		.andExpect(content().string(not(containsString("??????"))));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void NgWordIsBlank() throws Exception {
		mockMvc
		.perform(post("/registerNgWord").param("ngWord", "").with(csrf()))
		.andExpect(redirectedUrl("/toManagement"));
		
		mockMvc
		.perform(get("/toManagement").flashAttr("ngWordError", "1"))
		.andExpect(content().string(containsString("?????????????????????")));
	}

}
















