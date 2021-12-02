package com.example.bulletinboard.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.Posting;

@Repository
public interface PostingRepository extends JpaRepository<Posting, Long> {

	Posting findByNameAndThreadIdAndGenreId(String name, Long threadId, Integer genreId);
	
	@Query(value = "select posting_id from posting where thread_id = ?1 limit ?2 offset ?3", nativeQuery = true)
	List<Long> findPostingIdByThreadId(Long threadId, int limit, int currentpage);
	
	@Query(value = "select count(p) from Posting p where thread_id = ?1")
	int count_findByThreadId(Long threadId);
	
	@Query(value = "select count(p) from Posting p where thread_id =?1 and message like %?2%")
	int count_findMessageBySearch(long threadId, String message);
	
	@Modifying
	@Query(value = "delete from Posting p where thread_id = ?1")
	void deletePostingByThreadId(long threadId);
	
	@Modifying
	@Query(value = "delete from Posting p where genre_id = ?1")
	void deletePostingByGenreId(Integer genreId);
	
	@Query(value = "SELECT posting_id FROM posting p INNER JOIN thread t ON p.thread_id = t.thread_id "
			+ "WHERE p.genre_id = ?1 AND t.thread_title = ?2 AND p.posting_no = ?3", nativeQuery = true)
	Long findPostingIdByIdAndTitleAndNo(Integer genreId, String threadTitle, long postingNo);
	

}
