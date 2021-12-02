package com.example.bulletinboard.controller;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailSendException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bulletinboard.common.MailUtil;
import com.example.bulletinboard.form.MailData;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@SpringBootTest
@TestPropertySource(properties = {"spring.mail.host=localhost", "spring.mail.port=3025"})
@AutoConfigureMockMvc
@Transactional
class MailControllerTest2 {

	@Autowired
	MockMvc mockMvc;
	
	@MockBean
	MailUtil mailUtil;
	@InjectMocks
	private MailController mailController;
	
	private GreenMail greenMail;
	
	@BeforeEach
	public void before() {
		this.greenMail = new GreenMail(ServerSetupTest.SMTP);
		this.greenMail.start();
	}
	
	@AfterEach
	public void after() {
		this.greenMail.stop();
	}
	
	@Test
	void throwMailExceptionTest() throws Exception {
		//Arrange
		MailData mailData = new MailData();
		mailData.setName("名前");
		mailData.setEmail("from@example.com");
		mailData.setSubject("件名");
		mailData.setMessage("メッセージ");
		
		doThrow(new MailSendException("例外が発生しました")).when(mailUtil).sendMail(mailData);
		//Act and Assert
		mockMvc.perform(post("/mail/sendMail").flashAttr("mailData", mailData).with(csrf()))
		.andExpect(flash().attribute("failure", "failure"))
		.andExpect(redirectedUrl("/mail/toMailForm"));
		
	}
	

}













