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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.ReplyData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;

@SpringBootTest
@AutoConfigureMockMvc
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
	
	Genre genre;
	Thread thread;
	Posting posting;
	Reply reply;
	List<Reply> replyList;
	
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
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Replyが表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void ReplyForm画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/reply/toReplyForm/" + postingId))
		.andExpect(view().name("ReplyForm"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 新規Replyが作成されることを期待します() throws Exception {
		//Arrange
		ReplyData replyData = new ReplyData();
		replyData.setName("name");
		replyData.setReplyMessage("message");
		
		//Act and Assert
		mockMvc.perform(post("/reply/create/" + postingId).param("version", "0").flashAttr("replyData", replyData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/reply/showReply/" + postingId));
		
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("name")))
		.andExpect(content().string(containsString("message")))
		.andExpect(content().string(containsString("2.")))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ObjectOptimisticLockingFailureExceptionが発生したときform画面にリダイレクトされることを期待します() throws Exception {
		//Arrange
		ReplyData replyData = new ReplyData();
		//replyData.setReplyNo(2);
		replyData.setName("name");
		replyData.setReplyMessage("message");
		//Act and Assert
		mockMvc.perform(post("/reply/create/" + postingId).param("version", "1").flashAttr("replyData", replyData).with(csrf()))
		.andExpect(redirectedUrl("/reply/toReplyForm/" + postingId));
		;
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void Replyが削除されることを期待します() throws Exception {
		//Arrange
		Reply reply = replyRepository.findByNameAndPostingIdAndThreadIdAndGenreId("name", postingId, threadId, genreId);
		
		//Act and Assert
		mockMvc
		.perform(get("/reply/update/" + postingId + "/" + reply.getReplyId()))
		.andExpect(view().name("fragments/ReplyFragment :: replyFragment"));
		
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("削除")))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	@Sql({"/test2.sql"})
	@Transactional
	@WithMockUser(username = "user", roles = "USER")
	void Replyの書き込み制限に達したPostingでこれ以上コメントできませんと表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/reply/showReply/" + postingId).requestAttr("posting", posting))
		.andExpect(content().string(containsString("これ以上コメントできません")))
		.andExpect(view().name("Reply"));
	}
	
	@Test
	@Sql({"/test2.sql"})
	@Transactional
	@WithMockUser(username = "user", roles = "USER")
	void Replyの書き込み制限に達したPostingからURLの直接入力によりReplyのForm画面へ移動しようとした場合リダイレクトされることを期待します() throws Exception {
		mockMvc
		.perform(get("/reply/toReplyForm/" + postingId).requestAttr("posting", posting))
		.andExpect(redirectedUrl("/reply/showReply/" + postingId));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除_が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/reply/showReply/" + postingId))
		.andExpect(content().string(containsString("削除")))
		.andExpect(view().name("Reply"));
	}
	

}













