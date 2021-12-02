package com.example.bulletinboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.Inquiry;

@Repository
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
	
	@Query(value = "select count(i) from Inquiry i")
	int countInquiry();
	
	List<Inquiry> findByUsername(String username);
	
}
