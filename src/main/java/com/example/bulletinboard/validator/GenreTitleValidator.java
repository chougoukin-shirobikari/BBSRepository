package com.example.bulletinboard.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.example.bulletinboard.service.ValidationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GenreTitleValidator implements ConstraintValidator<GenreTitleIsUnique, String> {
	
	private final ValidationService validationService;
	
	@Override
	public void initialize(GenreTitleIsUnique annotation) {
	}
	
	//genre_titleに重複がないかチェックする
	@Override
	public boolean isValid(String genreTitle, ConstraintValidatorContext context) {
		 System.out.println(validationService + "GenreTitleValidator");
		boolean isNotUnique = validationService.genreTitleExist(genreTitle);
		if(!isNotUnique) {
			return true;
		}else {
			return false;
		}
	}
	

}
