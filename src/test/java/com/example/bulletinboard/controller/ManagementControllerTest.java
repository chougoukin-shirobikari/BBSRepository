package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
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

import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.UserInfoRepository;

@SpringBootTest
@AutoConfigureMockMvc
class ManagementControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	NgWordRepository ngWordRepository;
	@Autowired
	UserInfoRepository userInfoRepository;
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void NgWord関連画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/toNgWord"))
		.andExpect(content().string(containsString("ngword")))
		.andExpect(content().string(containsString("NGワードを追加")))
		.andExpect(view().name("fragments/NgWord :: ngWord"));
	}
	
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void NgWordが登録されることを期待します() throws Exception {
		mockMvc
		.perform(post("/registerNgWord").param("ngWord", "NGWORD").with(csrf()))
		.andExpect(view().name("fragments/NgWord :: ngWord"));
		
		mockMvc
		.perform(get("/toNgWord").flashAttr("ngWord", "ngWord"))
		.andExpect(content().string(containsString("NGワードを追加")))
		.andExpect(content().string(containsString("NGWORD")));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 選択したNgWordが削除されることを期待します() throws Exception {
		//Arrange
		NgWord ngWord = new NgWord();
		ngWord.setNgWord("NGWORD");
		ngWordRepository.saveAndFlush(ngWord);
		Integer ngWordId = ngWord.getNgWordId();
		//Act and Assert
		mockMvc
		.perform(get("/deleteNgWord/" + ngWordId))
		.andExpect(view().name("fragments/NgWord :: ngWord"));
		
		mockMvc
		.perform(get("/toNgWord").flashAttr("ngWord", "ngWord"))
		.andExpect(content().string(containsString("NGワードを追加")))
		.andExpect(content().string(not(containsString("NGWORD"))));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "user", roles = "ADMIN")
	void NgWordの登録で空の値が入力されたときエラーが表示されるかテストします() throws Exception {
		mockMvc
		.perform(post("/registerNgWord").param("ngWord", "").with(csrf()))
		.andExpect(view().name("fragments/NgWord :: ngWord"));
		
		mockMvc
		.perform(get("/toNgWord").flashAttr("ngWordError", "ngWordError"))
		.andExpect(content().string(containsString("エラー: 未入力 or 文字数が多すぎます")));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 管理画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/toManagement"))
		.andExpect(view().name("Management"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索関連画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/toSearch"))
		.andExpect(content().string(containsString("Posting")))
		.andExpect(view().name("fragments/Search :: search"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 会員情報画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/toUserInfo"))
		.andExpect(content().string(containsString("testuser")))
		.andExpect(view().name("fragments/UserInfo :: userInfo"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void userInfoが削除されることを期待します() throws Exception {
		//Arrange
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername("deleteduser");
		userInfo.setRole("USER");
		userInfo.setPassword("password");
		userInfo.setGender("男性");
		userInfoRepository.saveAndFlush(userInfo);
		Integer userId = userInfo.getUserId();
		//Act and Assert
		mockMvc
		.perform(get("/deleteUserInfo/" + userId).param("page", "1"))
		.andExpect(content().string(not(containsString("deleteduser"))))
		.andExpect(view().name("fragments/UserInfo :: userInfo"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 検索条件に一致したuserInfoが表示されることを期待します() throws Exception {
		//Arrange
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername("searcheduser");
		userInfo.setRole("USER");
		userInfo.setPassword("password");
		userInfo.setGender("男性");
		userInfoRepository.saveAndFlush(userInfo);
		//Act and Assert
		mockMvc
		.perform(post("/searchUsername").param("username", "searcheduser").with(csrf()))
		.andExpect(content().string(containsString("searcheduser")))
		.andExpect(content().string(not(containsString("testuser"))))
		.andExpect(view().name("fragments/UserInfo :: userInfo"));
		
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 三カ月間書き込みがないユーザーが表示されることを期待します() throws Exception {
		//Arrange
		UserInfo userInfo = new UserInfo();
		userInfo.setUsername("newuser");
		userInfo.setRole("USER");
		userInfo.setPassword("password");
		userInfo.setGender("男性");
		userInfoRepository.saveAndFlush(userInfo);
		//Act and Assert
		mockMvc
		.perform(get("/searchGhostUser"))
		.andExpect(content().string(containsString("ghostuser")))
		.andExpect(content().string(not(containsString("newuser"))))
		.andExpect(view().name("fragments/UserInfo :: userInfo"));
		
	}
	/**
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void お問い合わせが表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/toInquiry"))
		.andExpect(content().string(containsString("testuser")))
		.andExpect(content().string(containsString("message")))
		.andExpect(view().name("fragments/Inquiry :: inquiry"));
	}
	**/

}
















