package com.example.bulletinboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.bulletinboard.entity.UserInfo;

public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
	
	UserInfo findByUsername(String username);
	
	boolean existsByUsername(String username);
	
	@Query(value = "select u from UserInfo u where role = ?1")
	UserInfo findByRole(String role);
	
	@Query(value = "select count(u) from UserInfo u")
	int countUserInfo();
	
	@Query(value = "select count(u) from UserInfo u where username = ?1")
	int countUsername(String username);
	

}
