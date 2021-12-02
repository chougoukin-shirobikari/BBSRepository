package com.example.bulletinboard.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.bulletinboard.service.ValidationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NotNgWordValidator implements ConstraintValidator<NotNgWord, String> {
	
	private final ValidationService validationService;
	
	//入力された文字列にNGワードが含まれていないかをチェック
	@Override
	public boolean isValid(String msg, ConstraintValidatorContext context) {
		
		if(validationService.isNgWord(msg)) {
			return false;
			
		}else {
			return true;
		}
		
		
		
	}

}
