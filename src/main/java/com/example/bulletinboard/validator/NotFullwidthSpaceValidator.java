package com.example.bulletinboard.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotFullwidthSpaceValidator implements ConstraintValidator<NotFullwidthSpace, String> {
	
	
	//入力された文字列が全て全角スペースでないかをチェック
	@Override
	public boolean isValid(String str, ConstraintValidatorContext context) {
		boolean ans = true;
		
		if(str != null && !str.equals("")) {
			boolean isAllDoubleSpace = true;
			
			for(int i = 0; i < str.length(); i++) {
				if(str.charAt(i) != '　') {
					isAllDoubleSpace = false;
					break;
				}
			}
			if(isAllDoubleSpace) {
				ans = false;
			}
		}
		
		return ans;
	}
	

}
