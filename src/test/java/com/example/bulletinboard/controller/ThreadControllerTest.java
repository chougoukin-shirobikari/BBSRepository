package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;

@SpringBootTest
@AutoConfigureMockMvc
class ThreadControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	@Autowired
	GenreRepository genreRepository;
	@Autowired
	ThreadRepository threadRepository;
	@Autowired
	PostingRepository postingRepository;
	
	@MockBean
	AwsService awsService;
	@InjectMocks
	private ThreadController threadController;
	
	Genre genre;
	Thread thread;
	Integer genreId;
	
	@BeforeEach
	@Transactional
	public void before(){
		genre = genreRepository.findByGenreTitle("genre1");
		genreId = genre.getGenreId();
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Thread一覧画面が表示されることを期待します() throws Exception{
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(view().name("Thread"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void GetリクエストがAjaxで実行されたときThread一覧画面が表示されることを期待します() throws Exception{
		mockMvc
		.perform(get("/thread/showThreadByAjax/" + genreId))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Threadが新しい順に表示されることを期待します() throws Exception{
		mockMvc
		.perform(get("/thread/showThreadOrderByCreatedTime/" + genreId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread2", "thread1")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Threadが投稿件数が多い順に表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/showThreadOrderByNumberOfPosting/" + genreId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread1", "thread3")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Postリクエストで検索条件に一致したThreadが表示されることを期待します() throws Exception {
		mockMvc
		.perform(post("/thread/showThreadBySearch/" + genreId).param("threadTitle", "thread1").with(csrf()))
		.andExpect(content().string(containsString("thread1")))
		.andExpect(content().string(not(containsString("thread2"))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void GETリクエストで検索条件に一致したThreadが表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/showThreadBySearch/" + genreId).param("threadTitle", "thread1"))
		.andExpect(content().string(containsString("thread1")))
		.andExpect(content().string(not(containsString("thread2"))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void 検索条件に一致したThreadが投稿件数が多い順に表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/showThreadBySearchOrderByNumberOfPosting/" + genreId).param("threadTitle", "thread"))
		.andExpect(content().string(containsString("thread")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread1", "thread3")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void 検索条件に一致したThreadが新しい順に表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/showThreadBySearchOrderByCreatedTime/" + genreId).param("threadTitle", "thread"))
		.andExpect(content().string(containsString("thread")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread3","thread2", "thread1")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 新規Threadが作成されることを期待します() throws Exception {
		//Arrange
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("thread4");
		//Act and Assert
		mockMvc.perform(post("/thread/create/" + genreId).flashAttr("threadData", threadData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/thread/showThread/" + genreId));
	
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 入力されたスレッドタイトルが既に存在していた場合にForm画面にリダイレクトされることを期待します() throws Exception {
		//Arrange
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("thread1");
		//Act and Assert
		mockMvc.perform(post("/thread/create/" + genreId).flashAttr("threadData", threadData).with(csrf()))
		.andExpect(redirectedUrl("/thread/toThreadForm/" + genreId));
		
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void ThreadFormが表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/toThreadForm/" + genreId))
		.andExpect(view().name("ThreadForm"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 選択したThreadが削除されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		doNothing().when(awsService).deleteImageFromS3ByThreadId(thread.getThreadId());
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteThread/" + genreId + "/" + thread.getThreadId()))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deleteThreadでAmazonServiceExceptionが発生したときerror画面が表示されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		doThrow(new AmazonServiceException("エラーが発生しました")).when(awsService).deleteImageFromS3ByThreadId(thread.getThreadId());
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteThread/" + genreId + "/" + thread.getThreadId()))
		.andExpect(view().name("error"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 選択したThreadを削除した後Threadが新しい順に表示されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteThread/" + genreId + "/" + thread.getThreadId()).param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread2", "thread1")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 選択したThreadを削除した後Threadが投稿件数が多い順に表示されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteThread/" + genreId + "/" + thread.getThreadId()).param("orderBy", "orderByNumberOfPosting"))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread1", "thread2")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたThreadが削除されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteSearchedThread/" + genreId + "/" + thread.getThreadId()).param("bySearch", "yes").param("threadTitle", "thread"))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deleteSearchedThreadメソッドでAmazonServiceExceptionが発生したときerror画面が表示されることを期待します_4() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		doThrow(new AmazonServiceException("エラーが発生しました")).when(awsService).deleteImageFromS3ByThreadId(thread.getThreadId());
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteSearchedThread/" + genreId + "/" + thread.getThreadId()).param("bySearch", "yes").param("threadTitle", "thread"))
		.andExpect(view().name("error"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたThreadを削除した後Threadが投稿件数が多い順に表示されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteSearchedThread/" + genreId + "/" + thread.getThreadId()).param("bySearch", "yes").param("threadTitle", "thread").param("orderBy", "orderByNumberOfPosting"))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread1", "thread2")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたThreadを削除した後新しい順に表示されることを期待します() throws Exception {
		//Arrange
		thread = threadRepository.findByThreadTitleAndGenreId("thread3", genreId);
		//Act and Assert
		mockMvc
		.perform(get("/thread/deleteSearchedThread/" + genreId + "/" + thread.getThreadId()).param("bySearch", "yes").param("threadTitle", "thread").param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(not(containsString("thread3"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("thread2", "thread1")))))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
		
	}
	
	@Test
	@Sql({"/test1.sql"})
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 投稿件数が10件に達したThreadをすべて表示することを期待します() throws Exception {
		mockMvc
		.perform(get("/thread/showUnwritableThread/" + genreId))
		.andExpect(content().string(containsString("10件の投稿")))
		.andExpect(view().name("fragments/ThreadFragment :: threadFragment"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します () throws Exception {
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ADMINとしてログインしたとき_投稿可能件数を超えたスレッドを表示_が表示されることを期待します () throws Exception {
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(content().string(containsString("投稿可能件数を超えたスレッドを表示")))
		.andExpect(view().name("Thread"));
	}

}

























