package com.example.bulletinboard.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.Thread;

@Repository
public interface ThreadRepository extends JpaRepository<Thread, Long> {
	
	
	List<Thread> findByThreadTitle(String threadTitle);
	
	List<Thread> findAllByNumberOfPosting(Integer numberOfPosting);
	
	List<Thread> findByGenreId(Integer genreId);
	
	Thread findByThreadId(Long threadId);
	
	Thread findByThreadTitleAndGenreId(String threadTitle, Integer genreId);
	
	@Query(value = "select * from thread t where t.genre_id = ?1 limit ?2 offset ?3", nativeQuery = true)
	Page<Thread> findByGenreId(Integer genreId, int limit, int offset, Pageable pageable);
	
	@Query(value = "select count(t) from Thread t where genre_id =?1 ")
	int count_findByGenreId(Integer genreId);
	
	@Query(value = "select count(t) from Thread t where genre_id =?1 and thread_title like %?2%")
	int count_findThreadTitleBySearch(Integer genreId, String threadTitle);
	
	@Query(value = "select count(t) from Thread t where number_of_posting = ?1 and genre_id = ?2")
	int count_findUnwritableThread(Integer numberOfPosting, Integer genreId);
	
	@Query(value = "select thread_id from thread where genre_id = ?1 limit ?2 offset ?3",
			nativeQuery = true)
	List<Long> findThreadIdByGenreId(Integer genreId, int limit, int currentpage);
	
	@Modifying
	@Query(value = "delete from Thread t where genre_id = ?1")
	void deleteThreadByGenreId(Integer genreId);
	
	@Query(value = "SELECT thread_id FROM thread t INNER JOIN genre g ON t.genre_id = g.genre_id "
			+ "WHERE g.genre_title = ?1 AND t.thread_title = ?2", nativeQuery = true)
	Long findThreadIdByTitle(String genreTitle, String threadTitle);

	
}











