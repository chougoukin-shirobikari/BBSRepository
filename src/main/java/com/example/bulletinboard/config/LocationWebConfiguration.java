package com.example.bulletinboard.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class LocationWebConfiguration implements WebMvcConfigurer {
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//ローカル環境用
		//registry.addResourceHandler("/images/**").addResourceLocations("file:images/");
		//本番環境用
		registry.addResourceHandler("/images/**").addResourceLocations("file:/var/www/html/images/");
	}
	

}
