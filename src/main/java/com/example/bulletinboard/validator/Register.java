package com.example.bulletinboard.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Size;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
@Size(min = 2, max = 20, message = "２文字以上２０文字以内で入力してください")
@NotFullwidthSpace
@ReportAsSingleViolation
public @interface Register {
	
	String message() default "入力に全角スペースが含まれています";
	Class<?>[] groups() default{};
	Class<? extends Payload>[] payload() default{};
	

}
