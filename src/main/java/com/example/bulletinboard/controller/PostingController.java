package com.example.bulletinboard.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.PostingData;
import com.example.bulletinboard.model.PageInfo;
import com.example.bulletinboard.repository.ImageRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;
import com.example.bulletinboard.service.BulletinboardService;
import com.example.bulletinboard.service.PaginationService;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.Limit;
import com.example.bulletinboard.util.Mail;
import com.example.bulletinboard.util.MaxNumber;
import com.example.bulletinboard.util.Utils;

import lombok.RequiredArgsConstructor;

/**
 * Postingの表示、作成、削除に関する処理をするController
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/posting")
public class PostingController {
	
	private final PostingRepository postingRepository;
	private final ThreadRepository threadRepository;
	private final ReplyRepository replyRepository;
	private final ImageRepository imageRepository;
	private final PaginationService paginationService;
	private final ValidationService validationService;
	private final BulletinboardService bbService;
	//ローカル環境で使用
	//private final ImageService imageService;
	private final AwsService awsService;
	private final Mail mail;
	
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bulletinboardDaoImpl;
	@PostConstruct
	public void init() {
		bulletinboardDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	//1ページ当たりに表示するPostingの数を設定
	private final int limit = Limit.POSTING_LIMIT.getLimit();
	//1つのThreadに投稿できるPostingの数を設定
	private final int maxNumberOfPosting = MaxNumber.OF_POSTING.getMaxNumber();
	//1つのPostingに投稿できるReplyの数を設定
	private final int maxNumberOfReply = MaxNumber.OF_REPLY.getMaxNumber();
		
	
	//PostingForm画面を表示
	@GetMapping("/form/{threadId}")
	public ModelAndView showPostForm(@PathVariable(name = "threadId")long threadId,
			                   ModelAndView mv, Model model) {
		
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("postingData")) {
			mv.addObject("postingData", new PostingData());
		}
		
		//thread(エンティティオブジェクト)を取得しViewへ渡す
		Thread thread;
		try {
			thread = threadRepository.findById(threadId).get();
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		mv.addObject("thread", thread);
		
		//投稿制限に達したスレッドにPostingを投稿しようとした場合(URLの直接入力等で)リダイレクト
		if(thread.getNumberOfPosting() >= maxNumberOfPosting) {
			mv.setViewName("redirect:/posting/showPosting/" + thread.getThreadId());
			return mv;
		}
		
		mv.setViewName("PostingForm");
		return mv;
	}
	
	//投稿(Posting)を表示
	@GetMapping("/showPosting/{threadId}")
	public ModelAndView showPosting(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = postingRepository.count_findByThreadId(threadId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		List<Posting> postingList = bulletinboardDaoImpl.findByThreadIdAndPage(threadId, page);
		mv.addObject("postingList", postingList);
		
		mv.setViewName("Posting");
		return mv;
	}
	
	//投稿(Posting)をAjaxで表示
	@GetMapping("/showPostingByAjax/{threadId}")
	public ModelAndView showPostingByAjax(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = postingRepository.count_findByThreadId(threadId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = bulletinboardDaoImpl.findByThreadIdAndPage(threadId, page);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	//投稿(Posting)を表示(日付が新しい順)
	@GetMapping("/showPostingOrderByCreatedTime/{threadId}")
	public ModelAndView showPostingOrderByCreatedTimeByAjax(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = postingRepository.count_findByThreadId(threadId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//Postingの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByCreatedTime");
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = bulletinboardDaoImpl.findByThreadIdOrderbyDesc(threadId, page);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	
	
	//投稿(Posting)をPostリクエストでキーワード検索
	@PostMapping("/showPostingBySearch/{threadId}")
	public ModelAndView showPostingBySearchByAjaxWithPost(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page,
                                   @RequestParam(name = "message")String message, ModelAndView mv) {
		
		//messageのバリデーション
		if(message == null || message.isEmpty() || !validationService.isValid(message)) {
			//エラーがあった場合はメッセージを表示
			mv.addObject("isBlank", true);
		}
		
		//formから取得した文字列からキーワードを生成
		List<String> messageList = bbService.toSearchWords(message);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(messageList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("message", message);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = bulletinboardDaoImpl.count_findMessageBySearch(threadId, messageList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = bulletinboardDaoImpl.findByMessage(messageList,threadId, page);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	
	//投稿(Posting)をGetリクエストでキーワード検索
	@GetMapping("/showPostingBySearch/{threadId}")
	public ModelAndView showPostingBySearchByAjaxWithGet(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page,
                                   @RequestParam(name = "message")String message, ModelAndView mv) {
		//messageのバリデーション
		if(message == null || message.isEmpty() || !validationService.isValid(message)) {
			//エラーがあった場合はメッセージを表示
			mv.addObject("isBlank", true);
		}
		
		//formから取得した文字列からキーワードを生成
		List<String> messageList = bbService.toSearchWords(message);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(messageList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("message", message);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = bulletinboardDaoImpl.count_findMessageBySearch(threadId, messageList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = bulletinboardDaoImpl.findByMessage(messageList,threadId, page);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	
	//投稿(Posting)を検索→新しい順に並び替え
	@GetMapping("/showPostingBySearchOrderByCreatedTime/{threadId}")
	public ModelAndView showPostingBySearchOrderByCreatedTimeByAjax(@PathVariable(name = "threadId")long threadId,
                                   @RequestParam(name = "page", required = false)Integer page,
                                   @RequestParam(name = "message", required = false)String message, ModelAndView mv) {
		//messageのバリデーション
		if(message == null || message.isEmpty() || !validationService.isValid(message)) {
			//エラーがあった場合はメッセージを表示
			mv.addObject("isBlank", true);
		}
		//formから取得した文字列からキーワードを生成
		List<String> messageList = bbService.toSearchWords(message);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(messageList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("message", message);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = bulletinboardDaoImpl.count_findMessageBySearch(threadId, messageList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
		//Postingの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByCreatedTime");
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = bulletinboardDaoImpl.findByMessageOrderbyDesc(messageList,threadId, page);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	
	//書き込み処理
	@PostMapping("/createPosting/{threadId}")
	synchronized public String posting(@PathVariable(name = "threadId")long threadId,
			              @RequestParam(name = "version")long threadVersion,
			              @RequestParam(name = "file", required = false)MultipartFile file,
			              @ModelAttribute PostingData postingData,
			              BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		
		//nameとmessageのバリデーション
		String name = postingData.getName();
		String message = postingData.getMessage();
		validationService.nameIsValid(name, 12, result);
		validationService.messageIsValid(message, "message", 60, result);
		if(!result.hasErrors()) {
			//エラーなし
			//savePostingメソッドに必要な引数を取得
			Thread thread = threadRepository.findById(threadId).get();
			List<Posting> postingList = bulletinboardDaoImpl.findByThreadId(threadId);
			int size = postingList.size();
			try {
				//Postingを登録
				bbService.savePosting(threadVersion, thread, postingData, size, file);
				if(thread.getNumberOfPosting() == 10) {
					//投稿件数が制限に達した場合は管理人にメールで通知する
					mail.sendMailAboutPosting(thread.getThreadTitle());
				}
				//thread(エンティティオブジェクト)を取得しリダイレクト先へ渡す
	  			redirectAttributes.addFlashAttribute("thread", thread);
	  			//リダイレクト先でメッセージを表示する為にパラメータを渡す
				redirectAttributes.addFlashAttribute("isSuccessful", "isSuccessful");
	  			return "redirect:/posting/showPosting/" + thread.getThreadId();
				
			}catch(OptimisticLockingFailureException | IOException | MaxUploadSizeExceededException | AmazonServiceException | ParseException e) {
				//Postingの登録中にエラーがあった場合
				e.printStackTrace();
				//エラーメッセージをリダイレクト先で表示
				redirectAttributes.addFlashAttribute("Exception", true);
				return "redirect:/posting/form/" + threadId;
				
			}
			
		}else {
			//バリデーションでエラーあり
			//リダイレクト先でエラーメッセージを表示
			redirectAttributes.addFlashAttribute("postingData", postingData);
  			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.postingData", result);
  			return "redirect:/posting/form/" + threadId;
			
		}
		
	}
	//書き込みを削除("削除されました"と表示し、データベースからは削除しない)
	@Transactional
	@GetMapping("/deletePosting/{threadId}/{postingId}")
	public ModelAndView deletePosting(@PathVariable(name = "threadId")long threadId,
			                          @PathVariable(name = "postingId")long postingId,
			                             @RequestParam(name = "page", required = false)Integer page,
			                             @RequestParam(name = "bySearch", required = false)String bySearch,
			                             @RequestParam(name = "orderBy", required = false)String orderBy, ModelAndView mv) {
		
		//updeate(更新処理)するPostingを取得
		Posting posting = postingRepository.findById(postingId).get();
		
		//Postingに関連付けられたReplyがあれば削除
		if(posting.getNumberOfReply() != null) {
			replyRepository.deleteReplyByPostingId(postingId);
		}
		//Posgingに関連付けられたImageがあれば削除
		if(posting.getHasImage() != null) {
			//ローカル環境で使用
			//imageService.deleteImageByPostingId(postingId);
			try {
				awsService.deleteImageFromS3ByPostingId(postingId);
			}catch(AmazonServiceException e) {
				e.printStackTrace();
				mv.setViewName("error");
				return mv;
			}
			imageRepository.deleteByPostingId(postingId);
			posting.setHasImage(null);
		}
		//Postingにメッセージをセットし登録
		posting.setName("削除済み");
		posting.setWritingTime("削除済み");
		posting.setMessage("この投稿は削除されました");
		posting.setNumberOfReply(0);
		postingRepository.saveAndFlush(posting);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null ) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = postingRepository.count_findByThreadId(threadId);
		mv = bbService.createViewInfoAboutPosting(mv, page, total, limit, bySearch, orderBy);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = createPostingList(threadId, page, orderBy);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
		
	}
	
	//Postingを投稿日時の新しい順or古い順で取得
	private List<Posting> createPostingList(long threadId, Integer page, String orderBy){
		if(orderBy.equals("orderByCreatedTime")) {
			List<Posting> postingList = bulletinboardDaoImpl.findByThreadIdOrderbyDesc(threadId, page);
			return postingList;
		}else{
			List<Posting> postingList = bulletinboardDaoImpl.findByThreadIdAndPage(threadId, page);
			return postingList;
		}
	}
	
	//検索条件に一致したPostingの中から削除
	@Transactional
	@GetMapping("/deleteSearchedPosting/{threadId}/{postingId}")
	public ModelAndView deleteSearchedPosting(@PathVariable(name = "threadId")long threadId,
			                             @PathVariable(name = "postingId")long postingId,
			                             @RequestParam(name = "page", required = false)Integer page,
			                             @RequestParam(name = "message")String message,
			                             @RequestParam(name = "bySearch", required = false)String bySearch,
			                             @RequestParam(name = "orderBy", required = false)String orderBy, ModelAndView mv) {
		//updeate(更新処理)するPostingを取得
		Posting posting = postingRepository.findById(postingId).get();
		
		//Postingに関連付けられたReplyがあれば削除
		if(posting.getNumberOfReply() != null) {
			replyRepository.deleteReplyByPostingId(postingId);
		}
		//Posgingに関連付けられたImageがあれば削除
		if(posting.getHasImage() != null) {
			//ローカル環境で使用
			//imageService.deleteImageByPostingId(postingId);
			try {
				awsService.deleteImageFromS3ByPostingId(postingId);
			}catch(AmazonServiceException e) {
				e.printStackTrace();
				mv.setViewName("error");
				return mv;
			}
			imageRepository.deleteByPostingId(postingId);
			posting.setHasImage(null);
		}
		
		//Postingにメッセージをセットし登録
		posting.setName("削除済み");
		posting.setWritingTime("削除済み");
		posting.setMessage("この投稿は削除されました");
		posting.setNumberOfReply(0);
		postingRepository.saveAndFlush(posting);
		
		//formから取得した文字列からキーワードを生成
		List<String> messageList = bbService.toSearchWords(message);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(messageList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("message", message);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null ) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = bulletinboardDaoImpl.count_findMessageBySearch(threadId, messageList);
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		mv = bbService.createViewInfoAboutPosting(mv, page, total, limit, bySearch, orderBy);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//thread(エンティティオブジェクト), postingListを取得しViewへ渡す
		List<Posting> postingList = createSearchedPostingList(threadId, page, orderBy, messageList);
		mv.addObject("postingList", postingList);
		Thread thread = threadRepository.findById(threadId).get();
		mv.addObject("thread", thread);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
		
	}
	
	//キーワードに一致したPostingを投稿日時の新しい順or古い順で取得
	private List<Posting> createSearchedPostingList(long threadId, Integer page, String orderBy, List<String> messageList){
		if(orderBy.equals("orderByCreatedTime")) {
			List<Posting> postingList = bulletinboardDaoImpl.findByMessageOrderbyDesc(messageList,threadId, page);
			return postingList;
		}else{
			List<Posting> postingList = bulletinboardDaoImpl.findByMessage(messageList,threadId, page);
			return postingList;
		}
	}
	
	//画像を削除する
	@Transactional
	@GetMapping("/deletePostingImage/{postingId}")
	public ModelAndView deletePostingImage(@RequestParam(name = "page", required = false)Integer page,
			                        @PathVariable(name = "postingId")Long postingId,
		                            @RequestParam(name = "bySearch", required = false)String bySearch,
		                            @RequestParam(name = "orderBy", required = false)String orderBy, ModelAndView mv) {
		//画像ファイルを削除
		try {
			awsService.deleteImageFromS3ByPostingId(postingId);
		}catch(AmazonServiceException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//ローカル環境で使用
		//imageService.deleteImageByPostingId(postingId);
		
		//Imageエンティティを削除
		imageRepository.deleteByPostingId(postingId);
		
		//Imageと関連付けられていたPostingを更新(関連付けをリセット)
		Posting posting = postingRepository.findById(postingId).get();
		posting.setHasImage(null);
		postingRepository.saveAndFlush(posting);
		//Thread(エンティティオブジェクト)を取得しViewへ渡す
		Long threadId = posting.getThreadId();
		Thread thread = threadRepository.findByThreadId(threadId);
		mv.addObject("thread", thread);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null ) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = postingRepository.count_findByThreadId(threadId);
		mv = bbService.createViewInfoAboutPosting(mv, page, total, limit, bySearch, orderBy);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//postingListを取得しViewへ渡す
		List<Posting> postingList = createPostingList(threadId, page, orderBy);
		mv.addObject("postingList", postingList);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}
	
	//検索されたPostingの画像を削除する
	@Transactional
	@GetMapping("/deleteSearchedPostingImage/{postingId}")
	public ModelAndView deleteSearchedPostingImage(@RequestParam(name = "page", required = false)Integer page,
			                        @PathVariable(name = "postingId")Long postingId,
			                        @RequestParam(name = "message")String message,
		                            @RequestParam(name = "bySearch", required = false)String bySearch,
		                            @RequestParam(name = "orderBy", required = false)String orderBy, ModelAndView mv) {
		//画像ファイルを削除
		try {
			awsService.deleteImageFromS3ByPostingId(postingId);
		}catch(AmazonServiceException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//ローカル環境で使用
		//imageService.deleteImageByPostingId(postingId);
		
		//Imageエンティティを削除
		imageRepository.deleteByPostingId(postingId);
		
		//Imageと関連付けられていたPostingを更新(関連付けをリセット)
		Posting posting = postingRepository.findById(postingId).get();
		posting.setHasImage(null);
		postingRepository.saveAndFlush(posting);
		//Thread(エンティティオブジェクト)を取得しViewへ渡す
		Long threadId = posting.getThreadId();
		Thread thread = threadRepository.findByThreadId(threadId);
		mv.addObject("thread", thread);
		
		//formから取得した文字列からキーワードを生成
		List<String> messageList = bbService.toSearchWords(message);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(messageList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("message", message);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null ) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = postingRepository.count_findByThreadId(threadId);
		mv = bbService.createViewInfoAboutPosting(mv, page, total, limit, bySearch, orderBy);
		mv.addObject("maxNumberOfPosting", maxNumberOfPosting);
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		//postingListを取得しViewへ渡す
		List<Posting> postingList = createSearchedPostingList(threadId, page, orderBy, messageList);
		mv.addObject("postingList", postingList);
		
		mv.setViewName("fragments/PostingFragment :: postingFragment");
		return mv;
	}

}


















