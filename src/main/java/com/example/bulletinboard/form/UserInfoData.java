package com.example.bulletinboard.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserInfoData {
	
	private String username;
	
	private String password;
	
	private int gender;
	
	private boolean admin;
	

}
