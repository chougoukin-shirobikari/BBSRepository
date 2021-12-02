package com.example.bulletinboard.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.bulletinboard.entity.Inquiry;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.entity.UserInfo;

public interface BulletinBoardDao {
	
	List<Thread> findByGenreId(Integer genreId, int currentpage);
	
	List<Posting> findByThreadIdAndPage(Long threadId, int currentpage);
	
	List<Thread> findByGenreIdOrderbyDesc(Integer genreId, int currentpage);
	
	List<Thread> findByGenreIdOrderbyNumberOfPosting(Integer genreId, int currentpage);
	
	List<Thread> findByThreadTitle(List<String> threadTitleList, Integer genreId, int currentpage);
	
	List<Thread> findByThreadTitleOrderbyDesc(List<String> threadTitleList, Integer genreId, int currentpage);
	
	List<Thread> findByThreadTitleOrderbyNumberOfPosting(List<String> threadTitleList, Integer genreId, int currentpage);
	
	List<Thread> findUnwritableThread(Integer numberOfPosting, Integer genreId, int currentpage);
	
	List<Posting> findByThreadId(Long threadId);
		
	List<Reply> findByPostingId(Long postingId);
	
	Page<Posting> findByThreadId(long threadId, Pageable pageable);
	
	List<Posting> findByThreadIdOrderbyDesc(long threadId, int currentpage);
	
	List<Posting> findByMessage(List<String> messageList, long threadId, int currentpage);
	
	List<Posting> findByMessageOrderbyDesc(List<String> messageList, long threadId, int currentpage);
	
	Thread findByGenreIdAndThreadTitle(Integer genreId, String threadTitle);
	
	Posting findByThreadIdAndPostingNo(long threadId, String postingNo);
	
	Posting findByPostingData(Long threadId, String postingNo);
	
	Reply findByPostingIdAndReplyNo(long postingId, String replyNo);
	
	Reply findByReplyData(Long postingId, String replyNo);
	
	List<UserInfo> findUserInfo(Integer currentpage);
	
	List<UserInfo> findGhostUser(Integer currentpage);
	
	int countGhostUser(Integer currentpage);
	
	List<Inquiry> findInquiry(Integer currentpage);
	

}
