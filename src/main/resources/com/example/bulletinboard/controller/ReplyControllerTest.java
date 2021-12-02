package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class ReplyControllerTest {

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
	Posting posting;
	Reply reply;
	List<Reply> replyList;
	
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
		postingData1.setMessage("メッセージ");
		posting = postingData1.toEntity();
		posting.setThreadId(thread.getThreadId());
		//List<Posting> postingList = new ArrayList<>();
		//postingList.add(posting);
		//thread.setPostingList(postingList);
		postingRepository.saveAndFlush(posting);
		
		ReplyData replyData = new ReplyData();
		replyData.setReplyNo(0);
		replyData.setName("名無し");
		replyData.setReplyMessage("メッセージ");
		reply = replyData.toEntity();
		reply.setPostingId(posting.getPostingId());
		//replyList = new ArrayList<>();
		//replyList.add(reply);
		//posting.setReplyList(replyList);
		replyRepository.saveAndFlush(reply);
	}
	
	
	@Test
	void コメントを表示することを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		//Act and Assert
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	void ReplyForm画面を表示することを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		//Act and Assert
		mockMvc
		.perform(get("/reply/toReplyForm/" + postingId))
		.andExpect(view().name("ReplyForm"));
	}
	
	@Test
	void 新規コメントが作成されることを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		
		ReplyData replyData1 = new ReplyData();
		replyData1.setReplyNo(1);
		replyData1.setName("名無し");
		replyData1.setReplyMessage("メッセージ");
		
		//Act and Assert
		mockMvc.perform(post("/reply/create/" + postingId).flashAttr("replyData", replyData1).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/reply/showReply/" + postingId));
	}
	
	@Test
	void Posting画面を表示することを期待します() throws Exception {
		//Arrange
		Long threadId = thread.getThreadId();
		//Act and Assert
		mockMvc
		.perform(get("/reply/showPosting/" + threadId))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	void Replyが削除されることを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		
		//Act and Assert
		mockMvc
		.perform(get("/reply/update/" + postingId + "/" + reply.getReplyId()))
		.andExpect(redirectedUrl("/reply/showReply/" + postingId));
		
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("削除")))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	void これ以上投稿できませんと表示されることを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		
		for(int i = 0; i < 4; i++) {
			ReplyData replyData = new ReplyData();
			replyData.setReplyNo(i + 2);
			replyData.setName("名無し" + i + 2);
			replyData.setReplyMessage("メッセージ");
			Reply reply = replyData.toEntity();
			reply.setPostingId(posting.getPostingId());
			//replyList.add(reply);
			replyRepository.saveAndFlush(reply);
		}
		postingRepository.saveAndFlush(posting);
		//Act and Assert
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("これ以上コメントできません")))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	void ADMINとしてログインしたとき_削除_が表示されることを期待します() throws Exception {
		//Arrange
		long postingId = posting.getPostingId();
		//Act and Assert
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("削除")))
		.andExpect(view().name("Reply"));
	}
	

}













