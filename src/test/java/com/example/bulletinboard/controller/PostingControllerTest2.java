package com.example.bulletinboard.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.ImageService;

@SpringBootTest
@AutoConfigureMockMvc
class PostingControllerTest2 {
	
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
	ImageService imageService;
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
	void FileSizeLimitExceededExceptionが発生したときform画面にリダイレクトされる事を期待します() throws Exception {
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
		
		doThrow(new FileSizeLimitExceededException("例外が発生しました", 0 ,0)).when(imageService).doUploadingImage(file);
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/posting/form/" + threadId));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 画像のアップロードでIOExceptionが発生したときform画面にリダイレクトされることを期待します() throws Exception {
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
		
		doReturn(true).when(imageService).doUploadingImage((MultipartFile)any());
		doThrow(new IOException("例外が発生しました")).when(imageService).saveImageFile((Long)anyLong(), (MultipartFile)any());
		
		//Act and Assert
		mockMvc.perform(MockMvcRequestBuilders.multipart("/posting/createPosting/" + threadId).file(file).param("version", "0").flashAttr("postingData", postingData).with(csrf()))
		.andExpect(redirectedUrl("/posting/form/" + threadId));
		
	}
	
}




