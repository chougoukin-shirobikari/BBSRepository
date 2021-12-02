package com.example.bulletinboard.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.repository.UserInfoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	private final UserInfoRepository userInfoRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		UserInfo userInfo = userInfoRepository.findByUsername(username);
		if(userInfo == null) {
			throw new UsernameNotFoundException(username + "not found");
		}
		return createUserDetails(userInfo);
	}
	
	public User createUserDetails(UserInfo userInfo) {
		
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + userInfo.getRole()));
		
		return new User(userInfo.getUsername(), userInfo.getPassword(), grantedAuthorities);
	}
	
	

}












