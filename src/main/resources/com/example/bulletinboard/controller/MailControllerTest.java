package com.example.bulletinboard.controller;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class MailControllerTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	MailUtil mailUtil;
	
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
	void MailForm画面が表示されることを期待します() throws Exception {
		mockMvc
		.perform(get("/mail/toMailForm"))
		.andExpect(view().name("MailForm"));
	}
	
	@Test
	void sendMail() throws Exception {
		//Arrange
		MailData mailData = new MailData();
		mailData.setName("名前");
		mailData.setEmail("from@example.com");
		mailData.setSubject("件名");
		mailData.setMessage("メッセージ");
		//Act
		this.mailUtil.sendMail(mailData);
		MimeMessage actual = this.greenMail.getReceivedMessages()[0];
		//Assert
		assertThat(actual.getRecipients(RecipientType.TO)[0].toString(), is("banana0822hairuyo@gmail.com"));
		assertThat(actual.getReplyTo()[0].toString(), is("from@example.com"));
		assertThat(actual.getSubject(), is("件名"));
		assertThat(actual.getContent().toString(), is("メッセージ"));
		
		mockMvc.perform(post("/mail/sendMail").flashAttr("mailData", mailData).with(csrf()))
		.andExpect(flash().attribute("isSuccessful", "isSuccessful"))
		.andExpect(redirectedUrl("/mail/toMailForm"));
	}

}













