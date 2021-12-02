package com.example.bulletinboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.Reply;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {
	
	Reply findByNameAndPostingIdAndThreadIdAndGenreId(String name, long postingId, long threadId, Integer genreId);
	
	@Query(value = "select r from Reply r where posting_id = ?1")
	List<Reply> findByPostingId(long postingId);
	
	@Modifying
	@Query(value = "delete from Reply r where posting_id = ?1")
	void deleteReplyByPostingId(long postingId);
	
	@Modifying
	@Query(value = "delete from Reply r where thread_id = ?1")
	void deleteReplyByThreadId(long threadId);
	
	@Modifying
	@Query(value = "delete from Reply r where genre_id = ?1")
	void deleteReplyByGenreId(Integer genreId);

}
