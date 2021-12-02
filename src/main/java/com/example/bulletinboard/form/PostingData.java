package com.example.bulletinboard.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.bulletinboard.entity.Posting;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PostingData {
	
	private String name;
	
	private String writingTime;
	
	private String message;
	
	//入力データからPostingを生成して返す
	public Posting toEntity() {
		
		Posting posting = new Posting();
		
		posting.setName(this.name);
		
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
		this.writingTime = sdf.format(time);
		posting.setWritingTime(this.writingTime);
		
		posting.setMessage(this.message);
		
		//書き込んだuserのユーザー名を取得
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		posting.setUsername(username);
		
		posting.setPostingVersion(0);
		
		return posting;
	}
	

}
