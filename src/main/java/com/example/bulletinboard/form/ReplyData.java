package com.example.bulletinboard.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.bulletinboard.entity.Reply;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ReplyData {
	
	private String name;
	
	private String replyTime;
	
	private String replyMessage;
	
	//入力データからreplyを生成して返す
    public Reply toEntity() {
			
		Reply reply = new Reply();
		
		reply.setName(this.name);
			
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
		this.replyTime = sdf.format(time);
		reply.setReplyTime(this.replyTime);
			
		reply.setReplyMessage(this.replyMessage);
		
		//書き込んだuserのユーザー名を取得
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		reply.setUsername(username);
			
		return reply;
	}

}











