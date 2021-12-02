package com.example.bulletinboard.util;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Mail {
	
	private final MailSender mailSender;
	
	//投稿件数が10件に達したら管理人にメールで通知
	public void sendMailAboutPosting(String threadTitle) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo("banana0822hairuyo@gmail.com");
		mailMessage.setSubject("お知らせ");
		mailMessage.setText("スレッド:" + threadTitle + "の投稿件数が10件に達しました。");
		try {
			mailSender.send(mailMessage);
		}catch(MailException e) {
			e.printStackTrace();
		}
		
	}
	
	//お問い合わせがあったら管理人にメールで通知
	public void sendMailAboutInquiry(String username) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo("banana0822hairuyo@gmail.com");
		mailMessage.setSubject("お知らせ");
		mailMessage.setText("username:" + username + "さんからお問い合わせがありました。");
		try {
			mailSender.send(mailMessage);
		}catch(MailException e) {
			e.printStackTrace();
		}
	}

}
