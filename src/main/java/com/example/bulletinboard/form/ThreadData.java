package com.example.bulletinboard.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.validator.First;
import com.example.bulletinboard.validator.NotFullwidthSpace;
import com.example.bulletinboard.validator.NotNgWord;
import com.example.bulletinboard.validator.Second;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ThreadData {
	
	@NotBlank(message = "スレッドタイトルを入力してください", groups = First.class)
	@Size(max = 20, message = "２０文字以内で入力してください", groups = Second.class)
	@NotNgWord(groups = First.class)
	@NotFullwidthSpace(groups = First.class)
	private String threadTitle;
	
	private String createdTime;
	
	//入力データからthreadを生成して返す
    public Thread toEntity() {
		
		Thread thread = new Thread();
		
		thread.setThreadTitle(this.threadTitle);
		
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
		this.createdTime = sdf.format(time);
		thread.setCreatedTime(this.createdTime);
		
		//書き込んだuserのユーザー名を取得
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		thread.setUsername(username);
		
		thread.setThreadVersion(0);
		
		return thread;
	}
	

}
