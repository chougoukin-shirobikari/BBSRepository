package com.example.bulletinboard.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;

@SpringBootTest
@TestPropertySource(properties = {"spring.mail.host=localhost", "spring.mail.port=3025"})
class MailTest {
	
	@Autowired
	Mail mail;
	
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
	@WithMockUser(username = "user", roles = "USER")
	void Postingに関するメールの送信が成功することを期待します() throws Exception {
		//Arrange
		String threadTitle = "threadTitle";
		//Act
		this.mail.sendMailAboutPosting(threadTitle);
		MimeMessage actual = this.greenMail.getReceivedMessages()[0];
		//Assert
		assertThat(actual.getRecipients(RecipientType.TO)[0].toString(), is("banana0822hairuyo@gmail.com"));
		assertThat(actual.getContent().toString(), is("スレッド:" + threadTitle + "の投稿件数が10件に達しました。"));
	}
	
	@Test
	@WithMockUser(username = "user", roles = "USER")
	void お問い合わせに関するメールの送信が成功することを期待します() throws Exception {
		//Arrange
		String username = "user";
		//Act
		this.mail.sendMailAboutInquiry(username);
		MimeMessage actual = this.greenMail.getReceivedMessages()[0];
		//Assert
		assertThat(actual.getRecipients(RecipientType.TO)[0].toString(), is("banana0822hairuyo@gmail.com"));
		assertThat(actual.getContent().toString(), is("username:" + username + "さんからお問い合わせがありました。"));
	}
}




















