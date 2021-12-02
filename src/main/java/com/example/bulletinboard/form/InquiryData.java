package com.example.bulletinboard.form;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.bulletinboard.entity.Inquiry;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class InquiryData {
	
	private String username;
	
	private String message;
	
	private String inquiryTime;
	
	//入力データからInquiryを生成して返す
	public Inquiry toInquiry() {
		
		Inquiry inquiry = new Inquiry();
		
		inquiry.setUsername(this.username);
		inquiry.setMessage(this.message);
		
		Date time = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd (E) HH:mm");
		this.inquiryTime = sdf.format(time);
		inquiry.setInquiryTime(this.inquiryTime);
		
		return inquiry;
	}
	

}










