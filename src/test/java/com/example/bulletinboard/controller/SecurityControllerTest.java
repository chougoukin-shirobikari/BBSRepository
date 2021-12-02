package com.example.bulletinboard.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.form.UserInfoData;
import com.example.bulletinboard.repository.NgWordRepository;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	NgWordRepository ngWordRepository;
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void インデックスページとしてTopPage画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/"))
		.andExpect(view().name("TopPage"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void TopPage画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/TopPage"))
		.andExpect(view().name("TopPage"));
	}
	
	@Test
	void login画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/login"))
		.andExpect(view().name("login"));
		
	}
	
	@Test
	@Transactional
	void ログインしていない場合login画面にリダイレクトされることを期待します() throws Exception {
		mockMvc
		.perform(get("/TopPage"))
		.andExpect(redirectedUrl("http://localhost/login"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void ログアウトが成功することを期待します() throws Exception {
		mockMvc
		.perform(post("/logout").with(csrf()))
		.andExpect(redirectedUrl("/login"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void 新規登録画面が表示されることを期待します () throws Exception {
		mockMvc
		.perform(get("/register"))
		.andExpect(view().name("Register"));
	}
	
	@Test
	@Transactional
	void ユーザーの新規登録が成功することを期待します() throws Exception {
		//Arrange
		UserInfoData userInfoData = new UserInfoData();
		userInfoData.setUsername("user");
		userInfoData.setPassword("password");
		
		mockMvc
		.perform(post("/register").flashAttr("userInfoData", userInfoData).with(csrf()))
		.andExpect(redirectedUrl("/login"));
	}

}
















