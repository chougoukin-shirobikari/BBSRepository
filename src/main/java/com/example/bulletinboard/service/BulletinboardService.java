package com.example.bulletinboard.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.form.ReplyData;
import com.example.bulletinboard.model.PageInfo;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BulletinboardService {
	
	private final EntityManager entityManager;
	private final ImageService imageService;
	private final AwsService awsService;
	private final PaginationService paginationService;
	private final PostingRepository postingRepository;
	private final ThreadRepository threadRepository;
	private final ReplyRepository replyRepository;
	
	//postingを表示する際に必要なパラメータをModelにセットする(bySearch, orderByの値によって条件分岐)
	public ModelAndView createViewInfoAboutPosting(ModelAndView mv, Integer page, int total, int limit, String bySearch, String orderBy) {
		if(bySearch.equals("yes")) {
			if(orderBy.equals("orderByCreatedTime")) {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("bySearch", "yes");
				mv.addObject("orderBy", "orderByCreatedTime");
			}else {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("bySearch", "yes");
			}
		}else {
			if(orderBy.equals("orderByCreatedTime")) {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("orderBy", "orderByCreatedTime");
			}else {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
			}
		}
		
		return mv;
	}
	
	//threadを表示する際に必要なパラメータをModelにセットする(bySearch, orderByの値によって条件分岐)
	public ModelAndView createViewInfoAboutThread(ModelAndView mv, Integer page, int total, int limit, String bySearch, String orderBy) {
		if(bySearch.equals("yes")) {
			if(orderBy.equals("orderByCreatedTime")) {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("bySearch", "yes");
				mv.addObject("orderBy", "orderByCreatedTime");
			}else if(orderBy.equals("orderByNumberOfPosting")){
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("bySearch", "yes");
				mv.addObject("orderBy", "orderByNumberOfPosting");
			}else {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("bySearch", "yes");
			}
		}else {
			if(orderBy.equals("orderByCreatedTime")) {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("orderBy", "orderByCreatedTime");
			}else if(orderBy.equals("orderByNumberOfPosting")){
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
				mv.addObject("orderBy", "orderByNumberOfPosting");
			}else {
				PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
				mv.addObject("pageInfo", pageInfo);
			}
		}
		
		return mv;
	}
	
	//formに入力された値からキーワードのListを生成する
	public List<String> toSearchWords(String str){
		
		List<String> list = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		
		//半角スペース、全角スペースを削除しキーワードを取り出す
		sb.append(str.strip());
		while(sb.length() > 0) {
			int beginIndex1 = sb.indexOf("　");
			int beginIndex2 = sb.indexOf(" ");
			if(beginIndex1 < 0 && beginIndex2 > 0) {beginIndex1 = beginIndex2 + 1;}
			if(beginIndex2 < 0 && beginIndex1 > 0) {beginIndex2 = beginIndex1 + 1;}
			if(beginIndex1 < 0 && beginIndex2 < 0) {beginIndex1 = 0; beginIndex2 = 0;}
			String sbSubstring = null;
			if(beginIndex1 < beginIndex2) {
				sbSubstring = sb.substring(0, beginIndex1);
				list.add(sbSubstring);
				sb.delete(0, beginIndex1);
				String _str = sb.toString().strip();
				sb.setLength(0);
				sb.append(_str);
			}else if(beginIndex1 > beginIndex2) {
				sbSubstring = sb.substring(0, beginIndex2);
				list.add(sbSubstring);
				sb.delete(0, beginIndex2);
				String _str = sb.toString().strip();
				sb.setLength(0);
				sb.append(_str);
			}else {
				sbSubstring = sb.substring(0, sb.length());
				list.add(sbSubstring);
				sb.setLength(0);
				
			}
		}
		
		return list;
	}
	
	//postingをデータベースに登録する
	@Transactional
	public void savePosting(long threadVersion, Thread thread, PostingData postingData, int size, MultipartFile file) throws IOException, AmazonServiceException {
		
		//楽観ロックで排他制御
		if(threadVersion != thread.getThreadVersion()) {
			throw new ObjectOptimisticLockingFailureException(Thread.class, thread.getThreadId());
		}
		long threadId = thread.getThreadId();
		
		entityManager.lock(thread, LockModeType.OPTIMISTIC);
		Posting posting = postingData.toEntity();
		posting.setPostingNo(size + 1);
		posting.setNumberOfReply(0);
		posting.setThreadId(threadId);
		posting.setGenreId(thread.getGenreId());
		
		//画像のアップロードがある場合の処理
		boolean doUploadingImage = imageService.doUploadingImage(file);
		if(doUploadingImage == true) {
			posting.setHasImage(1);
			postingRepository.saveAndFlush(posting);
			imageService.saveImageFile(posting.getPostingId(), file);
			//S3にアップロードする
			//ローカル環境ではコメントアウト
			awsService.uploadImageToS3(posting.getPostingId());
		}else {
			postingRepository.saveAndFlush(posting);
			
		}
		
		//threadの情報を更新(postingの登録が成功したとき)
		thread.setNumberOfPosting(size + 1);
		threadRepository.saveAndFlush(thread);
		
	}
	
	//replyをデータベースに登録する
	@Transactional
	public void saveReply(long postingVersion, ReplyData replyData, Posting posting, int size) {
		
		//楽観ロックで排他制御
		if(postingVersion != posting.getPostingVersion()) {
			throw new ObjectOptimisticLockingFailureException(Posting.class, posting.getPostingId());
		}
		long postingId = posting.getPostingId();
		
		entityManager.lock(posting, LockModeType.OPTIMISTIC);
		Reply reply = replyData.toEntity();
		reply.setReplyNo(size + 1);
		reply.setPostingId(postingId);
		reply.setThreadId(posting.getThreadId());
		reply.setGenreId(posting.getGenreId());
		replyRepository.saveAndFlush(reply);
		
		//postingの情報を更新(replyの登録が成功したとき)
		posting.setNumberOfReply(size + 1);
		postingRepository.saveAndFlush(posting);
		
	}
}












