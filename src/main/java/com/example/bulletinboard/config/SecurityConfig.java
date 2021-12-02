package com.example.bulletinboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	private final UserDetailsService userDetailsService;
	//パスワードの暗号化
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/css/**", "/webjars/**", "/js/**", "/images/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.authorizeRequests()
		  .antMatchers("/login", "/register").permitAll()
		  .antMatchers("/toManagement", "/searchByPosting", "/searchByReply", "/registerNgWord", "/deleteNgWord/**").hasRole("ADMIN")
		  .anyRequest().authenticated()
		  .and()
		  .formLogin()
		  .loginPage("/login")
		  .defaultSuccessUrl("/TopPage")
		  .and()
		  .logout()
		  .logoutUrl("/logout")
		  .logoutSuccessUrl("/login");
		
		
		http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
		
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder anth) throws Exception {
		anth
		.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder());
		
		/**
		 .inMemoryAuthentication()
		 .withUser("admin")
		 .password(passwordEncoder().encode("password"))
		 .authorities("ROLE_ADMIN")
		 .and()
		 .withUser("user")
		 .password(passwordEncoder().encode("password"))
		 .authorities("ROLE_USER");
		 **/
	}
	

}


























