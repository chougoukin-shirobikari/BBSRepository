package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.entity.Inquiry;
import com.example.bulletinboard.form.InquiryData;
import com.example.bulletinboard.repository.InquiryRepository;

@SpringBootTest
@AutoConfigureMockMvc
class InquiryControllerTest {
	
	@Autowired
	MockMvc mockMvc;
	@Autowired
	InquiryRepository inquiryRepository;
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void InquiryForm画面が表示されることを期待します() throws Exception{
		mockMvc
		.perform(get("/inquiry/toInquiryForm"))
		.andExpect(view().name("InquiryForm"));
	}
	
	@Test
	@Transactional
	@WithMockUser(username = "testuser", roles = "USER")
	void お問い合わせの送信が成功することを期待します() throws Exception{
		//Arrange
		InquiryData inquiryData = new InquiryData();
		inquiryData.setUsername("testuser");
		inquiryData.setMessage("message");
		//Act and Assert
		mockMvc
		.perform(post("/inquiry/makeAnInquiry").flashAttr("inquiryData", inquiryData).with(csrf()))
		.andExpect(model().hasNoErrors())
		.andExpect(redirectedUrl("/inquiry/toInquiryForm"));
	}
	
	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void 選択したお問い合わせが削除されることを期待します() throws Exception {
		//Arrange
		List<Inquiry> inquiryList = inquiryRepository.findByUsername("testuser");
		Inquiry inquiry = inquiryList.get(0);
		//Act and Assert
		mockMvc
		.perform(get("/inquiry/deleteInquiry/" + inquiry.getInquiryId()))
		.andExpect(content().string(not(containsString("testuser"))))
		.andExpect(view().name("fragments/Inquiry :: inquiry"));
	} 

}



















