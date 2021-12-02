package com.example.bulletinboard.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.bulletinboard.service.ValidationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UsernameIsUniqueValidator implements ConstraintValidator<UsernameIsUnique, String> {
	
	private final ValidationService validationService;
	
	//ユーザ名に重複がないかをチェックする
	@Override
	public boolean isValid(String username, ConstraintValidatorContext context) {
		boolean isNotUnique = validationService.usernameExist(username);
		if(!isNotUnique) {
			return true;
		}else {
			return false;
		}
	}
	

}
