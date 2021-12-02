package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;

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
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ThreadRepository;

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
	
	Genre genre = new Genre();
	
	
	@BeforeEach
	@Transactional
	public void before() {
		
		genre.setGenreTitle("ジャンル名");
		genreRepository.saveAndFlush(genre);
		ThreadData threadData1 = new ThreadData();
		threadData1.setThreadTitle("スレッド名1");
		Thread thread1 = threadData1.toEntity();
		thread1.setGenreId(genre.getGenreId());
		thread1.setNumberOfPosting(1);
		//List<Thread> threadList1 = new ArrayList<>();
		//threadList1.add(thread1);
		//genre.setThreadList(threadList1);
		threadRepository.saveAndFlush(thread1);
		
		PostingData postingData1 = new PostingData();
		postingData1.setPostingNo(1L);
		postingData1.setName("名無し1");
		postingData1.setMessage("メッセージ");
		Posting posting1 = postingData1.toEntity();
		posting1.setThreadId(thread1.getThreadId());
		//List<Posting> postingList1 = new ArrayList<>();
		//postingList1.add(posting1);
		//thread1.setPostingList(postingList1);
		postingRepository.saveAndFlush(posting1);
		
		ThreadData threadData2 = new ThreadData();
		threadData2.setThreadTitle("スレッド名2");
		Thread thread2 = threadData2.toEntity();
		thread2.setGenreId(genre.getGenreId());
		thread2.setNumberOfPosting(2);
		//List<Thread> threadList2 = new ArrayList<>();
		//threadList2.add(thread1);
		//genre.setThreadList(threadList2);
		threadRepository.saveAndFlush(thread2);
		
		PostingData postingData2 = new PostingData();
		postingData2.setPostingNo(1L);
		postingData2.setName("名無し2-1");
		postingData2.setMessage("メッセージ");
		Posting posting2 = postingData2.toEntity();
		posting2.setThreadId(thread2.getThreadId());
		//List<Posting> postingList2 = new ArrayList<>();
		//postingList2.add(posting2);
		PostingData postingData3 = new PostingData();
		postingData3.setPostingNo(2L);
		postingData3.setName("名無し2-2");
		postingData3.setMessage("メッセージ");
		Posting posting3 = postingData3.toEntity();
		posting3.setThreadId(thread2.getThreadId());
		//postingList2.add(posting3);
		//thread2.setPostingList(postingList2);
		postingRepository.saveAndFlush(posting2);
		
		ThreadData threadData3 = new ThreadData();
		threadData3.setThreadTitle("スレッド名3");
		Thread thread3 = threadData3.toEntity();
		thread3.setGenreId(genre.getGenreId());
		thread3.setNumberOfPosting(1);
		//List<Thread> threadList3 = new ArrayList<>();
		//threadList3.add(thread3);
		//genre.setThreadList(threadList3);
		threadRepository.saveAndFlush(thread3);
		
		PostingData postingData4 = new PostingData();
		postingData4.setPostingNo(1L);
		postingData4.setName("名無し3");
		postingData4.setMessage("メッセージ");
		Posting posting4 = postingData4.toEntity();
		posting4.setThreadId(thread3.getThreadId());
		//List<Posting> postingList3 = new ArrayList<>();
		//postingList3.add(posting4);
		//thread3.setPostingList(postingList3);
		postingRepository.saveAndFlush(posting4);
	}

	
	@Test
	@Transactional
	void Getリクエストが実行されたときThread表示することを期待します() throws Exception{
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(view().name("Thread"));
		
	}
	
	@Test
	@Transactional
	void スレッドを新しい順に並び変えることを期待します() throws Exception{
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThreadOrderByCreatedTime/" + genreId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("スレッド名2", "スレッド名1")))))
		.andExpect(view().name("Thread"));
	}
	
	
	@Test
	@Transactional
	void 投稿件数が多い順に表示することを期待します() throws Exception {
		
		//Arrange
		Integer genreId = genre.getGenreId();
		
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThreadOrderByNumberOfPosting/" + genreId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("スレッド名2", "スレッド名1")))))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	void 検索条件に一致したThreadを表示することを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThreadBySearch/" + genreId).param("threadTitle", "スレッド名1"))
		.andExpect(content().string(containsString("スレッド名1")))
		.andExpect(content().string(not(containsString("スレッド名2"))))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	void 検索条件に一致したThreadを投稿件数が多い順に並び変えて表示することを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThreadBySearchOrderByNumberOfPosting/" + genreId).param("threadTitle", "スレッド"))
		.andExpect(content().string(containsString("スレッド")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("スレッド名2", "スレッド名1")))))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	void 検索条件に一致したThreadを新しい順に並び変えて表示することを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThreadBySearchOrderByCreatedTime/" + genreId).param("threadTitle", "スレッド"))
		.andExpect(content().string(containsString("スレッド")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("スレッド名3","スレッド名2", "スレッド名1")))))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	void 新規Threadが作成されることを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("新規スレッド");
		//Act and Assert
		mockMvc.perform(post("/thread/create/" + genreId).flashAttr("threadData", threadData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/thread/showThread/" + genreId));
	
	}
	@Test
	@Transactional
	void threadTitleIsNotUnique() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		ThreadData threadData = new ThreadData();
		threadData.setThreadTitle("スレッド名1");
		//Act and Assert
		mockMvc.perform(post("/thread/create/" + genreId).flashAttr("threadData", threadData).with(csrf()))
		.andExpect(redirectedUrl("/thread/toThreadForm/" + genreId));
		
	}

	@Test
	@Transactional
	void ThreadFormが表示されることを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		//Act and Assert
		mockMvc
		.perform(get("/thread/toThreadForm/" + genreId))
		.andExpect(view().name("ThreadForm"));
	}
	
	@Test
	@Transactional
	void 選択したThreadが削除されることを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		ThreadData threadData4 = new ThreadData();
		threadData4.setThreadTitle("スレッド名4");
		Thread thread4 = threadData4.toEntity();
		thread4.setGenreId(genreId);
		thread4.setNumberOfPosting(1);
		//List<Thread> threadList4 = new ArrayList<>();
		//threadList4.add(thread4);
		//genre.setThreadList(threadList4);
		threadRepository.saveAndFlush(thread4);
		
		PostingData postingData5 = new PostingData();
		postingData5.setPostingNo(1L);
		postingData5.setName("名無し1");
		postingData5.setMessage("メッセージ");
		Posting posting1 = postingData5.toEntity();
		posting1.setThreadId(thread4.getThreadId());
		//List<Posting> postingList4 = new ArrayList<>();
		//postingList4.add(posting1);
		//thread4.setPostingList(postingList4);
		postingRepository.saveAndFlush(posting1);
		//Act and Assert
		mockMvc
		.perform(get("/thread/delete/" + genreId + "/" + thread4.getThreadId()))
		.andExpect(content().string(not(containsString("スレッド名4"))))
		.andExpect(redirectedUrl("/thread/showThread/" + genreId));
	}
	
	@Test
	@Transactional
	void 投稿件数が10件に達したThreadをすべて表示することを期待します() throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		ThreadData threadData4 = new ThreadData();
		threadData4.setThreadTitle("スレッド名4");
		Thread thread4 = threadData4.toEntity();
		thread4.setGenreId(genreId);
		thread4.setNumberOfPosting(10);
		//List<Thread> threadList4 = new ArrayList<>();
		//threadList4.add(thread4);
		//genre.setThreadList(threadList4);
		
		//List<Posting> postingList = new ArrayList<>();
		for(int i = 0; i < 10; i++) {
			PostingData postingData = new PostingData();
			postingData.setPostingNo((long)(i + 1));
			postingData.setName("名無し" + (i + 1));
			postingData.setMessage("メッセージ");
			Posting posting = postingData.toEntity();
			posting.setThreadId(thread4.getThreadId());
			//postingList.add(posting);
			postingRepository.saveAndFlush(posting);
		}
		//thread4.setPostingList(postingList);
		threadRepository.saveAndFlush(thread4);
		//Act and Assert
		mockMvc
		.perform(get("/thread/showUnwritableThread/" + genreId))
		.andExpect(content().string(containsString("10件の投稿")))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "user", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します () throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Thread"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "user", roles = "ADMIN")
	void ADMINとしてログインしたとき_投稿可能件数を超えたスレッドを表示_が表示されることを期待します () throws Exception {
		//Arrange
		Integer genreId = genre.getGenreId();
		
		//Act and Assert
		mockMvc
		.perform(get("/thread/showThread/" + genreId))
		.andExpect(content().string(containsString("投稿可能件数を超えたスレッドを表示")))
		.andExpect(view().name("Thread"));
	}

}

























