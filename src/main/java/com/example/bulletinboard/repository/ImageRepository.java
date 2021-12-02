package com.example.bulletinboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Integer> {
	
	Image findByPostingId(Long postingId);
	
	void deleteByPostingId(Long postingId);
	
	List<Image> findByThreadId(Long threadId);
	
	void deleteByThreadId(Long threadId);
	
	List<Image> findByGenreId(Integer genreId);
	
	void deleteByGenreId(Integer genreId);
	

}
