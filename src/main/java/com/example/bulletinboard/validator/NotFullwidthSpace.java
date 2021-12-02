package com.example.bulletinboard.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE,ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotFullwidthSpaceValidator.class)
public @interface NotFullwidthSpace {
	
	String message() default "入力に全角スペースが含まれています";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
	

}
