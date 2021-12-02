package com.example.bulletinboard.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.bulletinboard.entity.Inquiry;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.util.Limit;
import com.example.bulletinboard.util.Utils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BulletinBoardDaoImpl implements BulletinBoardDao {
	
	private final EntityManager entityManager;
	
	private final int posting_limit = Limit.POSTING_LIMIT.getLimit();
	private final int thread_limit = Limit.THREAD_LIMIT.getLimit();
	private final int userInfo_limit = Limit.USERINFO_LIMIT.getLimit();
	private final int inquiry_limit = Limit.INQUIRY_LIMIT.getLimit();
	
	//genre_idでthreadを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByGenreId(Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = ?1 limit ?2 offset ?3";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter(1, genreId);
		query.setParameter(2, thread_limit);
		query.setParameter(3, offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//thread_idでpostingを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Posting> findByThreadId(Long threadId){
		String qstr = "select * from posting where thread_id = ?1";
		Query query = entityManager.createNativeQuery(qstr, Posting.class);
		query.setParameter(1, threadId);
		List<Posting> list = query.getResultList();
		return list;
	}
	
	//thread_idでpostingを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Posting> findByThreadIdAndPage(Long threadId, int currentpage){
		int offset = currentpage * posting_limit;
		String qstr = "select * from posting where thread_id = ?1 limit ?2 offset ?3";
		Query query = entityManager.createNativeQuery(qstr, Posting.class);
		query.setParameter(1, threadId);
		query.setParameter(2, posting_limit);
		query.setParameter(3, offset);
		List<Posting> list = query.getResultList();
		return list;
	}
	
	//posting_idでreplyを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Reply> findByPostingId(Long postingId){
		String qstr = "select * from reply r where r.posting_id = ?1";
		Query query = entityManager.createNativeQuery(qstr, Reply.class);
		query.setParameter(1, postingId);
		List<Reply> list = query.getResultList();
		return list;
	}
	
	//threadを日付が新しい順(thread_idの降順)で取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByGenreIdOrderbyDesc(Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = ?1 order by thread_id desc limit ?2 offset ?3";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter(1, genreId);
		query.setParameter(2, thread_limit);
		query.setParameter(3, offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//threadを投稿件数の多い順で取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByGenreIdOrderbyNumberOfPosting(Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = ?1 order by number_of_posting desc limit ?2 offset ?3";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter(1, genreId);
		query.setParameter(2, thread_limit);
		query.setParameter(3, offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//書き込み件数を超えたthreadを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findUnwritableThread(Integer numberOfPosting, Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where number_of_posting = ?1 and genre_id = ?2 limit ?3 offset ?4";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter(1, numberOfPosting);
		query.setParameter(2, genreId);
		query.setParameter(3, thread_limit);
		query.setParameter(4, offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//スレッドタイトルを検索条件にthreadを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByThreadTitle(List<String> threadTitleList, Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = :genre_id"
				+ Utils.makeThreadTitleQueryString(threadTitleList)
				+ " limit :limit offset :offset";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter("genre_id", genreId);
		
		//キーワード(threadTitle)の数に合わせてLike句を動的に生成
		if(threadTitleList.size() == 1) {
			query.setParameter("thread_title1", "%" + threadTitleList.get(0) + "%");
		}else if(threadTitleList.size() > 1) {
			for(int i = 0; i < threadTitleList.size(); i++) {
				String threadTitle = "thread_title" + (i + 1);
				query.setParameter(threadTitle, "%" + threadTitleList.get(i) + "%");
			}
		}else {
			
			
		}
		query.setParameter("limit", thread_limit);
		query.setParameter("offset", offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//threadTitleでthreadを検索→日付が新しい順に並び替えて取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByThreadTitleOrderbyDesc(List<String> threadTitleList, Integer genreId, int currentpage){
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = :genre_id"
				+ Utils.makeThreadTitleQueryString(threadTitleList)
				+ " order by thread_id desc limit :limit offset :offset";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter("genre_id", genreId);
		
		//キーワード(threadTitle)の数に合わせてLike句を動的に生成
		if(threadTitleList.size() == 1) {
			query.setParameter("thread_title1", "%" + threadTitleList.get(0) + "%");
		}else if(threadTitleList.size() > 1) {
			for(int i = 0; i < threadTitleList.size(); i++) {
				String threadTitle = "thread_title" + (i + 1);
				query.setParameter(threadTitle, "%" + threadTitleList.get(i) + "%");
			}
		}else {
			
		}
		query.setParameter("limit", thread_limit);
		query.setParameter("offset", offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//threadTitleでthreadを検索→投稿件数が多い順に並び替えて取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Thread> findByThreadTitleOrderbyNumberOfPosting(List<String> threadTitleList, Integer genreId, int currentpage){
		//final int limit = 5;
		int offset = currentpage * thread_limit;
		String qstr = "select * from thread where genre_id = :genre_id"
				+ Utils.makeThreadTitleQueryString(threadTitleList)
				+ " order by number_of_posting desc limit :limit offset :offset";
		Query query = entityManager.createNativeQuery(qstr, Thread.class);
		query.setParameter("genre_id", genreId);
		
		//キーワード(threadTitle)の数に合わせてLike句を動的に生成
		if(threadTitleList.size() == 1) {
			query.setParameter("thread_title1", "%" + threadTitleList.get(0) + "%");
		}else if(threadTitleList.size() > 1) {
			for(int i = 0; i < threadTitleList.size(); i++) {
				String threadTitle = "thread_title" + (i + 1);
				query.setParameter(threadTitle, "%" + threadTitleList.get(i) + "%");
			}
		}else {
			
		}
		query.setParameter("limit", thread_limit);
		query.setParameter("offset", offset);
		List<Thread> list = query.getResultList();
		return list;
	}
	
	//threadTitleをキーワードに検索し取得したthreadの数をカウント
	public int count_findThreadTitleBySearch(Integer genreId, List<String> threadTitleList) {
		String qstr = "select count(*) from thread where genre_id = :genre_id"
				+ Utils.makeThreadTitleQueryString(threadTitleList);
		Query query = entityManager.createNativeQuery(qstr);
		query.setParameter("genre_id", genreId);
		
		//キーワード(threadTitle)の数に合わせてLike句を動的に生成
		if(threadTitleList.size() == 1) {
			query.setParameter("thread_title1", "%" + threadTitleList.get(0) + "%");
		}else if(threadTitleList.size() > 1) {
			for(int i = 0; i < threadTitleList.size(); i++) {
				String threadTitle = "thread_title" + (i + 1);
				query.setParameter(threadTitle, "%" + threadTitleList.get(i) + "%");
			}
		}else {
			
		}
		int count = ((Number)query.getSingleResult()).intValue();
		return count;
	}
	
	//日付が新しい順(posting_idの降順)でpostingを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Posting> findByThreadIdOrderbyDesc(long threadId, int currentpage){
		int offset = currentpage * posting_limit;
		String qstr = "select * from posting where thread_id = ?1 order by posting_id desc limit ?2 offset ?3";
		Query query = entityManager.createNativeQuery(qstr, Posting.class);
		query.setParameter(1, threadId);
		query.setParameter(2, posting_limit);
		query.setParameter(3, offset);
		List<Posting> list = query.getResultList();
		return list;
	}
	
	//messageでpostingを検索し取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Posting> findByMessage(List<String> messageList, long threadId, int currentpage){
		int offset = currentpage * posting_limit;
		String qstr = "select * from posting where thread_id = :thread_id"
				+ Utils.makeMessageQueryString(messageList)
				+ " limit :limit offset :offset";
		Query query = entityManager.createNativeQuery(qstr, Posting.class);
		query.setParameter("thread_id", threadId);
		
		//キーワード(message)の数に合わせてLike句を動的に生成
		if(messageList.size() == 1) {
			query.setParameter("message1", "%" + messageList.get(0) + "%");
		}else if(messageList.size() > 1) {
			for(int i = 0; i < messageList.size(); i++) {
				String message = "message" + (i + 1);
				query.setParameter(message, "%" + messageList.get(i) + "%");
			}
		}else {
			
		}
		query.setParameter("limit", posting_limit);
		query.setParameter("offset", offset);
		List<Posting> list = query.getResultList();
		return list;
	}
	
	//messageでpostingを検索→日付が新しい順に並び替えて取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Posting> findByMessageOrderbyDesc(List<String> messageList, long threadId, int currentpage){
		int offset = currentpage * posting_limit;
		String qstr = "select * from posting where thread_id = :thread_id"
				+ Utils.makeMessageQueryString(messageList)
				+ " order by posting_id desc limit :limit offset :offset";
		Query query = entityManager.createNativeQuery(qstr, Posting.class);
		query.setParameter("thread_id", threadId);
		
		//キーワード(message)の数に合わせてLike句を動的に生成
		if(messageList.size() == 1) {
			query.setParameter("message1", "%" + messageList.get(0) + "%");
		}else if(messageList.size() > 1) {
			for(int i = 0; i < messageList.size(); i++) {
				String message = "message" + (i + 1);
				query.setParameter(message, "%" + messageList.get(i) + "%");
			}
		}else {
			
		}
		query.setParameter("limit", posting_limit);
		query.setParameter("offset", offset);
		List<Posting> list = query.getResultList();
		return list;
	}
	
	//messageをキーワードに検索したpostingの数をカウント
	public int count_findMessageBySearch(long threadId, List<String> messageList) {
		String qstr = "select count(*) from posting where thread_id = :thread_id"
				+ Utils.makeMessageQueryString(messageList);
		Query query = entityManager.createNativeQuery(qstr);
		query.setParameter("thread_id", threadId);
		
		//キーワード(message)の数に合わせてLike句を動的に生成
		if(messageList.size() == 1) {
			query.setParameter("message1", "%" + messageList.get(0) + "%");
		}else if(messageList.size() > 1) {
			for(int i = 0; i < messageList.size(); i++) {
				String message = "message" + (i + 1);
				query.setParameter(message, "%" + messageList.get(i) + "%");
			}
		}else {
			
		}
		int count = ((Number)query.getSingleResult()).intValue();
		return count;
	}
	
	//thread_idでpostingを取得
	@SuppressWarnings("unchecked")
	@Override
	public Page<Posting> findByThreadId(long threadId, Pageable pageable){
		String qstr = "select p from Posting p where thread_id = ?1";
		Query query = entityManager.createQuery(qstr).setParameter(1, threadId);
		
		int totalRows = query.getResultList().size();
		query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
		query.setMaxResults(pageable.getPageSize());
		
		Page<Posting> page = new PageImpl<Posting>(query.getResultList(), pageable, totalRows);
		
		return page;
	}
	
	//genre_idとthread_titleでthreadを取得
	@Override
	public Thread findByGenreIdAndThreadTitle(Integer genreId, String threadTitle) {
		String qstr = "select t from Thread t where genre_id = ?1 and thread_title = ?2";
		Query query = entityManager.createQuery(qstr).setParameter(1, genreId).setParameter(2, threadTitle);
		Thread thread = (Thread)query.getSingleResult();
		return thread;
	}
	
	//thread_idとposting_noでpostingを取得
	@Override
	public Posting findByThreadIdAndPostingNo(long threadId, String postingNo) {
		String qstr = "select p from Posting p where thread_id = ?1 and posting_no = ?2";
		Query query = entityManager.createQuery(qstr).setParameter(1, threadId).setParameter(2, postingNo);
		Posting posting = (Posting)query.getSingleResult();
		return posting;
		
	}
	
	//thread_idとposting_noでpostingを取得
	@Override
	public Posting findByPostingData(Long threadId, String postingNo) {
		String qstr = "select p from Posting p where thread_id = ?1 and posting_no = ?2";
		Query query = entityManager.createQuery(qstr).setParameter(1, threadId).setParameter(2, postingNo);
		Posting posting = (Posting)query.getSingleResult();
		return posting;
	}
	
	//posting_idとreply_noでreplyを取得
	@Override
	public Reply findByPostingIdAndReplyNo(long postingId, String replyNo) {
		String qstr = "select r from Reply r where posting_id = ?1 and reply_no = ?2";
		Query query = entityManager.createQuery(qstr).setParameter(1, postingId).setParameter(2, replyNo);
		Reply reply = (Reply)query.getSingleResult();
		return reply;
		
	}
	
	//posting_idとreply_noでreplyを取得
	@Override
	public Reply findByReplyData(Long postingId, String replyNo) {
		String qstr = "select r from Reply r where posting_id = ?1 and reply_no = ?2";
		Query query = entityManager.createQuery(qstr).setParameter(1, postingId).setParameter(2, replyNo);
		Reply reply = (Reply)query.getSingleResult();
		return reply;
	}
	
	//userInfoを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<UserInfo> findUserInfo(Integer currentpage){
		int offset = currentpage * userInfo_limit;
		String qstr = "select * from user_info limit ?1 offset ?2";
		Query query = entityManager.createNativeQuery(qstr, UserInfo.class);
		query.setParameter(1, userInfo_limit);
		query.setParameter(2, offset);
		List<UserInfo> list = query.getResultList();
		return list;
	}
	
	//三カ月以上書き込みのないuserInfoを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<UserInfo> findGhostUser(Integer currentpage){
		int offset = currentpage * userInfo_limit;
		String timeThreeMonthsAgo = Utils.createTimeThreeMonthsAgo();
		String qstr = "select * from user_info where last_writing_time < " +  "'" + timeThreeMonthsAgo + "'"
				    + " order by last_writing_time desc limit ?1 offset ?2";
		Query query = entityManager.createNativeQuery(qstr, UserInfo.class);
		query.setParameter(1, userInfo_limit);
		query.setParameter(2, offset);
		List<UserInfo> list = query.getResultList();
		return list;
	}
	
	//三カ月以上書き込みのないuserInfoの数を取得
	@Override
	public int countGhostUser(Integer currentpage) {
		String timeThreeMonthsAgo = Utils.createTimeThreeMonthsAgo();
		String qstr = "select count(*) from user_info where last_writing_time < " + "'" + timeThreeMonthsAgo + "'";
		Query query = entityManager.createNativeQuery(qstr);
		int count = ((Number)query.getSingleResult()).intValue();
		return count;
	}
	
	//inquiryを取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Inquiry> findInquiry(Integer currentpage){
		int offset = currentpage * inquiry_limit;
		String qstr = "select * from inquiry order by inquiry_id desc limit ?1 offset ?2";
		Query query = entityManager.createNativeQuery(qstr, Inquiry.class);
		query.setParameter(1, inquiry_limit);
		query.setParameter(2, offset);
		List<Inquiry> list = query.getResultList();
		return list;
	}
	

}














