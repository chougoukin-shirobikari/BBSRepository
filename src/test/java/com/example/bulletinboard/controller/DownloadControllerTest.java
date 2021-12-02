package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.service.AwsService;

@SpringBootTest
@AutoConfigureMockMvc
class DownloadControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	AwsService awsService;
	@InjectMocks
	private DownloadController downloadController;
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void downloadメソッドで期待した値が返ってくるかかをテストします() throws Exception {
		
		//Arrange
		Long postingId = 1L;
		String expectedPath = "expectedPath";
		doReturn("expectedPath").when(awsService).downloadImageFromS3(postingId);
		
		//Act
		String response = mockMvc.perform(get("/download/" + postingId))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		//Assert
		assertThat(response, is(expectedPath));
		
	}

}












