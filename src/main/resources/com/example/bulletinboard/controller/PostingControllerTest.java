package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.form.ReplyData;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PostingControllerTest {
	
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
	
	Thread thread;
	
	@BeforeEach
	public void before() {
		Genre genre = new Genre();
		genre.setGenreTitle("ジャンル名");
		genreRepository.saveAndFlush(genre);
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("スレッド名");
		thread = threadData.toEntity();
		thread.setGenreId(genre.getGenreId());
		thread.setNumberOfPosting(1);
		//List<Thread> threadList = new ArrayList<>();
		//threadList.add(thread);
		//genre.setThreadList(threadList);
		threadRepository.saveAndFlush(thread);
		
		PostingData postingData1 = new PostingData();
		postingData1.setPostingNo(1L);
		postingData1.setName("名無し1");
		postingData1.setMessage("メッセージ1");
		Posting posting = postingData1.toEntity();
		posting.setThreadId(thread.getThreadId());
		//List<Posting> postingList = new ArrayList<>();
		//postingList.add(posting);
		//thread.setPostingList(postingList);
		postingRepository.saveAndFlush(posting);
		
		ReplyData replyData = new ReplyData();
		replyData.setReplyNo(0);
		replyData.setName("名無し");
		replyData.setReplyMessage("メッセージ");
		Reply reply = replyData.toEntity();
		reply.setPostingId(posting.getPostingId());
		//List<Reply> replyList = new ArrayList<>();
		//replyList.add(reply);
		//posting.setReplyList(replyList);
		replyRepository.saveAndFlush(reply);
		
		PostingData postingData2 = new PostingData();
		postingData2.setPostingNo(1L);
		postingData2.setName("名無し2");
		postingData2.setMessage("メッセージ2");
		Posting posting2 = postingData2.toEntity();
		posting2.setThreadId(thread.getThreadId());
		//postingList.add(posting2);
		//thread.setPostingList(postingList);
		postingRepository.saveAndFlush(posting2);
		
		ReplyData replyData2 = new ReplyData();
		replyData2.setReplyNo(0);
		replyData2.setName("名無し");
		replyData2.setReplyMessage("メッセージ");
		Reply reply2 = replyData2.toEntity();
		reply2.setPostingId(posting.getPostingId());
		//List<Reply> replyList2 = new ArrayList<>();
		//replyList2.add(reply2);
		//posting2.setReplyList(replyList2);
		replyRepository.saveAndFlush(reply2);
	}

	
	@Test
	void PostingForm画面を表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		//Act and Assert
		mockMvc
		.perform(get("/posting/form/" + threadId).param("size", "0"))
		.andExpect(view().name("PostingForm"));
	}
	
	@Test
	void 投稿を表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(view().name("Posting"));
		
	}
	
	@Test
	void 投稿を新しい順に表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPostingOrderByCreatedTime/" + threadId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("名無し2", "名無し1")))))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	void 検索条件に一致した投稿を表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPostingBySearch/" + threadId).param("message", "メッセージ1"))
		.andExpect(content().string(containsString("メッセージ1")))
		.andExpect(content().string(not(containsString("メッセージ2"))))
		.andExpect(view().name("Posting"));
		
	}
	
	@Test
	void 検索条件に一致した投稿を新しい順に並び変えて表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPostingBySearchOrderByCreatedTime/" + threadId).param("message", "メッセージ"))
		.andExpect(content().string(containsString("メッセージ")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("メッセージ2", "メッセージ1")))))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	void 新規Postingが作成されることを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		PostingData postingData3 = new PostingData();
		postingData3.setPostingNo(1L);
		postingData3.setName("名無し1");
		postingData3.setMessage("メッセージ1");
		List<Posting> postingList3 = new ArrayList<>();
		for(Posting posting : postingList3) {
			posting.setThreadId(threadId);
		}
		//thread.setPostingList(postingList3);
		//Act and Assert
		mockMvc.perform(post("/posting/createPosting/" + threadId + "/" + 1).flashAttr("postingData", postingData3).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
	}
	
	@Test
	void 投稿が削除されることを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		PostingData postingData3 = new PostingData();
		postingData3.setPostingNo(1L);
		postingData3.setName("名無し1");
		postingData3.setMessage("メッセージ");
		Posting posting3 = postingData3.toEntity();
		posting3.setThreadId(thread.getThreadId());
		List<Posting> postingList3 = new ArrayList<>();
		postingList3.add(posting3);
		for(Posting posting : postingList3) {
			posting.setThreadId(threadId);
		}
		//thread.setPostingList(postingList3);
		postingRepository.saveAndFlush(posting3);
		
		ReplyData replyData3 = new ReplyData();
		replyData3.setReplyNo(0);
		replyData3.setName("名無し");
		replyData3.setReplyMessage("メッセージ");
		Reply reply3 = replyData3.toEntity();
		reply3.setPostingId(posting3.getPostingId());
		//List<Reply> replyList3 = new ArrayList<>();
		//replyList3.add(reply3);
		//posting3.setReplyList(replyList3);
		replyRepository.saveAndFlush(reply3);
		//Act and Assert
		mockMvc
		.perform(get("/posting/updatePost/" + threadId + "/" + posting3.getPostingId()))
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
		
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("削除")))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	void これ以上投稿できませんと表示されることを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		
		//List<Posting> postingList3 = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			PostingData postingData3 = new PostingData();
			postingData3.setPostingNo(1L);
			postingData3.setName("名無し1");
			postingData3.setMessage("メッセージ");
			Posting posting3 = postingData3.toEntity();
			posting3.setThreadId(threadId);
			//postingList3.add(posting3);
			postingRepository.saveAndFlush(posting3);
			
			ReplyData replyData3 = new ReplyData();
			replyData3.setReplyNo(0);
			replyData3.setName("名無し");
			replyData3.setReplyMessage("メッセージ");
			Reply reply3 = replyData3.toEntity();
			reply3.setPostingId(posting3.getPostingId());
			//List<Reply> replyList3 = new ArrayList<>();
			//replyList3.add(reply3);
			//posting3.setReplyList(replyList3);
			replyRepository.saveAndFlush(reply3);
		}
		//thread.setPostingId(postingId);
		threadRepository.saveAndFlush(thread);
		
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("これ以上投稿できません")))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		//Act and Assert
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Posting"));
	}

}


















