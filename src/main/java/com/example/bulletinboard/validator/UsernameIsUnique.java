package com.example.bulletinboard.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UsernameIsUniqueValidator.class)
public @interface UsernameIsUnique {
	
	String message() default "エラー:ユーザー名の重複";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
	

}
