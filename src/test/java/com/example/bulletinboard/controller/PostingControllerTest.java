package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;

@SpringBootTest
@AutoConfigureMockMvc
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
	@WithMockUser(username = "user", roles = "USER")
	void PostingForm画面を表示することを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/form/" + threadId))
		.andExpect(view().name("PostingForm"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Posting一覧画面を表示することを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(view().name("Posting"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void GetリクエストがAjaxで実行されたときPostingを表示することを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPostingByAjax/" + threadId))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Postingが新しい順に表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPostingOrderByCreatedTime/" + threadId))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("name2", "name1")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Postリクエストで検索条件に一致したPostingが表示されることを期待します() throws Exception {
		mockMvc
		.perform(post("/posting/showPostingBySearch/" + threadId).param("message", "message1").with(csrf()))
		.andExpect(content().string(containsString("message1")))
		.andExpect(content().string(not(containsString("message2"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void Getリクエストで検索条件に一致したPostingが表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPostingBySearch/" + threadId).param("message", "message1"))
		.andExpect(content().string(containsString("message1")))
		.andExpect(content().string(not(containsString("message2"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void 検索条件に一致したPostingが新しい順で表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPostingBySearchOrderByCreatedTime/" + threadId).param("message", "message"))
		.andExpect(content().string(containsString("message")))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("message2", "message1")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 新規Postingが作成されることを期待します_画像のアップロードなし() throws Exception {
		//Arrange
		PostingData postingData = new PostingData();
		postingData.setName("name4");
		postingData.setMessage("message4");
		byte[] testImage = null;
		MockMultipartFile file = new MockMultipartFile("file", "textImage", null, testImage);
		
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
		
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("name4")))
		.andExpect(content().string(containsString("message4")))
		.andExpect(content().string(containsString("4.")))
		.andExpect(view().name("Posting"));
		
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 新規Postingが作成されることを期待します_画像のアップロードあり() throws Exception {
		//Arrange
		PostingData postingData = new PostingData();
		postingData.setName("name4");
		postingData.setMessage("message4");
		
		byte[] testImage = null;
		File imageFile = new File("src/test/resources/spring-boot.jpg");
		Path path = Paths.get(imageFile.getAbsolutePath());
		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			testImage = Files.readAllBytes(path);
		}
		MockMultipartFile file = new MockMultipartFile("file", "spring-boot.jpg", null, testImage);
		doNothing().when(awsService).uploadImageToS3((Long)anyLong());
		
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
		
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("name4")))
		.andExpect(content().string(containsString("message4")))
		.andExpect(content().string(containsString("4.")))
		.andExpect(content().string(containsString("画像を表示する")))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 画像以外のファイルをアップロードした時form画面にリダイレクトされることを期待します() throws Exception {
		//Arrange
		PostingData postingData = new PostingData();
		postingData.setName("name4");
		postingData.setMessage("message4");
		
		String testText = "テスト";
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", null, testText.getBytes());
		
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/posting/form/" + threadId));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ObjectOptimisticLockingFailureExceptionが発生した時form画面にリダイレクトされることを期待します() throws Exception {
		//Arrange
		PostingData postingData = new PostingData();
		postingData.setName("name3");
		postingData.setMessage("message3");
		//Act and Assert
		mockMvc.perform(post("/posting/createPosting/" + threadId).param("version", "1").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(redirectedUrl("/posting/form/" + threadId));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void uploadImageToS3メソッドでAmazonServiceExceptionが発生したときerror画面が表示されることを期待します() throws Exception {
		//Arrange
		PostingData postingData = new PostingData();
		postingData.setName("name3");
		postingData.setMessage("message3");
		
		byte[] testImage = null;
		File imageFile = new File("src/test/resources/spring-boot.jpg");
		Path path = Paths.get(imageFile.getAbsolutePath());
		if(Files.exists(path, LinkOption.NOFOLLOW_LINKS)) {
			testImage = Files.readAllBytes(path);
		}
		MockMultipartFile file = new MockMultipartFile("file", "spring-boot.jpg", null, testImage);
		doThrow(new AmazonServiceException("エラーが発生しました")).when(awsService).uploadImageToS3((Long)anyLong());
		
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(redirectedUrl("/posting/form/" + threadId));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void Postingが削除されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deletePosting/" + threadId + "/" + posting.getPostingId()))
		.andExpect(content().string(containsString("削除")))
		.andExpect(content().string(not(containsString("name1"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deletePostingメソッドでAmazonServiceExceptionが発生したときerror画面が表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doThrow(new AmazonServiceException("エラーが発生しました")).when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deletePosting/" + threadId + "/" + posting.getPostingId()))
		.andExpect(view().name("error"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void Postingを削除した後新しい順にPostingが表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deletePosting/" + threadId + "/" + posting.getPostingId()).param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(containsString("削除")))
		.andExpect(content().string(not(containsString("name1"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("name3", "name2", "削除済み")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
		
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたPostingが削除されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deleteSearchedPosting/" + threadId + "/" + posting.getPostingId()).param("bySearch", "yes").param("message", "message"))
		.andExpect(content().string(containsString("削除")))
		.andExpect(content().string(not(containsString("message1"))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void deleteSearchedPostingメソッドでAmazonServiceExceptionが発生したときerror画面が表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doThrow(new AmazonServiceException("エラーが発生しました")).when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deleteSearchedPosting/" + threadId + "/" + posting.getPostingId()).param("bySearch", "yes").param("message", "message"))
		.andExpect(view().name("error"));
		
	}
	
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索されたPostingを削除した後Postingが新しい順に表示されることを期待します() throws Exception {
		//Arrange
		Posting posting = postingRepository.findByNameAndThreadIdAndGenreId("name1", threadId, genreId);
		doNothing().when(awsService).deleteImageFromS3ByPostingId(posting.getPostingId());
		//Act and Assert
		mockMvc
		.perform(get("/posting/deleteSearchedPosting/" + threadId + "/" + posting.getPostingId()).param("bySearch", "yes").param("message", "message").param("orderBy", "orderByCreatedTime"))
		.andExpect(content().string(not(containsString("message1"))))
		.andExpect(content().string(is(stringContainsInOrder(Arrays.asList("name3", "name2")))))
		.andExpect(view().name("fragments/PostingFragment :: postingFragment"));
		
	}
	
	
	@Test
	@Transactional
	@Sql({"/test1.sql"})
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 書き込み制限に達したThreadでこれ以上投稿できませんと表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPosting/" + threadId).requestAttr("thread", thread))
		.andExpect(content().string(containsString("これ以上投稿できません")))
		.andExpect(view().name("Posting"));
	}
	
	@Test
	@Transactional
	@Sql({"/test1.sql"})
	@WithMockUser(username = "admin", roles = "ADMIN")
	void Postingの書き込み制限に達したThreadからURLの直接入力によりPostingのForm画面へ移動しようとした場合リダイレクトされることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/form/" + threadId))
		.andExpect(redirectedUrl("/posting/showPosting/" + threadId));
	}
	
	
	@Test
	@WithMockUser(username = "user", roles = "ADMIN")
	void ADMINとしてログインしたとき_削除する_が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/posting/showPosting/" + threadId))
		.andExpect(content().string(containsString("削除する")))
		.andExpect(view().name("Posting"));
	}

}


















