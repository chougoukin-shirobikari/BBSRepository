package com.example.bulletinboard.controller;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.entity.Thread;
import com.example.bulletinboard.form.ThreadData;
import com.example.bulletinboard.model.PageInfo;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.ImageRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
//import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;
import com.example.bulletinboard.service.BulletinboardService;
import com.example.bulletinboard.service.PaginationService;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.Limit;
import com.example.bulletinboard.util.Utils;
import com.example.bulletinboard.validator.All;

import lombok.RequiredArgsConstructor;

/**
 * Threadの表示、作成、削除に関する処理をするController
 * 
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/thread")
public class ThreadController {
	
	private final PostingRepository postingRepository;
	private final ThreadRepository threadRepository;
	private final GenreRepository genreRepository;
	private final ReplyRepository replyRepository;
	private final ImageRepository imageRepository;
	private final PaginationService paginationService;
	private final ValidationService validationService;
	private final BulletinboardService bbService;
	//ローカル環境で使用
	//private final ImageService imageService;
	private final AwsService awsService;
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bulletinboardDaoImpl;
	@PostConstruct
	public void init() {
		bulletinboardDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	//1ページ当たりに表示するThreadの数を設定
	private final int limit = Limit.THREAD_LIMIT.getLimit();
	
	//スレッドの表示
	@GetMapping("/showThread/{genreId}")
	public ModelAndView showThread(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = threadRepository.count_findByGenreId(genreId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = bulletinboardDaoImpl.findByGenreId(genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("Thread");
		return mv;
	}
	
	//スレッドをAjaxで表示
	@GetMapping("/showThreadByAjax/{genreId}")
	public ModelAndView showThreadByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = threadRepository.count_findByGenreId(genreId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = bulletinboardDaoImpl.findByGenreId(genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	//スレッドの表示(新しい順)
	@GetMapping("/showThreadOrderByCreatedTime/{genreId}")
	public ModelAndView showThreadOrderByCreatedTimeByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = threadRepository.count_findByGenreId(genreId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//threadの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByCreatedTime");
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = bulletinboardDaoImpl.findByGenreIdOrderbyDesc(genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	//スレッドの表示(投稿件数が多い順)
	@GetMapping("/showThreadOrderByNumberOfPosting/{genreId}")
	public ModelAndView showThreadOrderByNumberOfPostingByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = threadRepository.count_findByGenreId(genreId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//threadの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByNumberOfPosting");
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = bulletinboardDaoImpl.findByGenreIdOrderbyNumberOfPosting(genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}

	
	//検索条件に一致したスレッドをPOSTで表示
	@PostMapping("/showThreadBySearch/{genreId}")
	public ModelAndView showThreadBySearchByAjaxWithPost(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page,
			                       @RequestParam(name = "threadTitle", required = false)String threadTitle,
			                       ModelAndView mv) {
		
		//threadTitleのバリデーション
		if(threadTitle == null || threadTitle.isEmpty() || !validationService.isValid(threadTitle)) {
			mv.addObject("isBlank", true);
		}
		//formから取得した文字列からキーワードを生成
		List<String> threadTitleList = bbService.toSearchWords(threadTitle);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(threadTitleList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("threadTitle", threadTitle);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = bulletinboardDaoImpl.count_findThreadTitleBySearch(genreId, threadTitleList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
        
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
        List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitle(threadTitleList, genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	//検索条件に一致したスレッドをGETリクエストで表示
	@GetMapping("/showThreadBySearch/{genreId}")
	public ModelAndView showThreadBySearchByAjaxWithGet(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page,
			                       @RequestParam(name = "threadTitle", required = false)String threadTitle,
			                       ModelAndView mv) {
		//treadTitleのバリデーション
		if(threadTitle == null || threadTitle.isEmpty() || !validationService.isValid(threadTitle)) {
			//エラーがあった場合はメッセージを表示
			mv.addObject("isBlank", true);
		}
		////formから取得した文字列からキーワードを生成
		List<String> threadTitleList = bbService.toSearchWords(threadTitle);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(threadTitleList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("threadTitle", threadTitle);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = bulletinboardDaoImpl.count_findThreadTitleBySearch(genreId, threadTitleList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
        
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
        List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitle(threadTitleList, genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	

	//検索条件に一致したスレッドを表示→投稿件数が多い順に並び替え
	@GetMapping("/showThreadBySearchOrderByNumberOfPosting/{genreId}")
	public ModelAndView showThreadBySearchOrderByNumberOfPostingByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page,
			                       @RequestParam(name = "threadTitle", required = false)String threadTitle,
			                       ModelAndView mv) {
		//threadTitleのバリデーション
		if(threadTitle == null || threadTitle.isEmpty() || !validationService.isValid(threadTitle)) {
			mv.addObject("isBlank", true);
		}
		//formから取得した文字列からキーワードを生成
		List<String> threadTitleList = bbService.toSearchWords(threadTitle);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(threadTitleList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("threadTitle", threadTitle);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = bulletinboardDaoImpl.count_findThreadTitleBySearch(genreId, threadTitleList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
		//threadの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByNumberOfPosting");
        
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
        List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitleOrderbyNumberOfPosting(threadTitleList, genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	//検索条件に一致したスレッドを表示→日付が新しい順に並び替え
	@GetMapping("/showThreadBySearchOrderByCreatedTime/{genreId}")
	public ModelAndView showThreadBySearchOrderByCreatedTimeByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page,
			                       @RequestParam(name = "threadTitle", required = false)String threadTitle,
			                       ModelAndView mv) {
		//threadTitleのバリデーション
		if(threadTitle == null || threadTitle.isEmpty() || !validationService.isValid(threadTitle)) {
			mv.addObject("isBlank", true);
		}
		//formから取得した文字列からキーワードを生成
		List<String> threadTitleList = bbService.toSearchWords(threadTitle);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(threadTitleList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("threadTitle", threadTitle);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null) {page = 0;}
		int total = bulletinboardDaoImpl.count_findThreadTitleBySearch(genreId, threadTitleList);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		//検索結果画面の表示に必要なパラメータをViewへ渡す
		mv.addObject("bySearch", "yes");
		//threadの並び替えに必要なパラメータをViewへ渡す
		mv.addObject("orderBy", "orderByCreatedTime");
        
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
        List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitleOrderbyDesc(threadTitleList, genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	
	//書き込み可能件数を超えたスレッドをまとめて表示
	@GetMapping("/showUnwritableThread/{genreId}")
	public ModelAndView showUnwritableThreadByAjax(@PathVariable(name = "genreId")Integer genreId,
			                       @RequestParam(name = "page", required = false)Integer page, ModelAndView mv) {
		
		//ページングに必要な情報を取得しViewへ渡す
		page = 0;
		int total = threadRepository.count_findUnwritableThread(10, genreId);
		PageInfo pageInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfo", pageInfo);
		mv.addObject("unwritable", 0);
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = bulletinboardDaoImpl.findUnwritableThread(10, genreId, page);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	
	//スレッドを作成
	@Transactional
	@PostMapping("/create/{genreId}")
	public synchronized String threadCreate(@PathVariable(name = "genreId")int genreId,
			                         @ModelAttribute(name = "threadData")@Validated(All.class) ThreadData threadData,
			                         BindingResult result,
			                         RedirectAttributes redirectAttributes) {
		//threadDataのバリデーション
		boolean isUnique = validationService.isUnique(genreId, threadData.getThreadTitle());
		if(!result.hasErrors() && isUnique) {
			//エラーなし
			//threadを登録
			Genre genre = genreRepository.findById(genreId).get();
  			Thread thread = threadData.toEntity();
  			thread.setGenreId(genreId);
  			thread.setNumberOfPosting(0);
  			threadRepository.saveAndFlush(thread);
  			//genre(エンティティオブジェクト)を取得しリダイレクト先へ渡す
  			redirectAttributes.addFlashAttribute("genre", genre);
  			return "redirect:/thread/showThread/" + genre.getGenreId();
			
		}else if(!isUnique) {
			//エラーあり(タイトルが重複)
			//エラー情報をセット
  			FieldError fieldError = new FieldError(result.getObjectName(),
                      "threadTitle", "エラー:スレッドタイトルの重複");
  			result.addError(fieldError);
  			//リダイレクト先でエラーメッセージを表示
  			redirectAttributes.addFlashAttribute("threadData", threadData);
  			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.threadData", result);
  			return "redirect:/thread/toThreadForm/" + genreId;
			
		}else {
			//エラーあり(タイトルの重複以外のエラー)
			//リダイレクト先でエラーメッセージを表示
			redirectAttributes.addFlashAttribute("threadData", threadData);
  			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.threadData", result);
  			return "redirect:/thread/toThreadForm/" + genreId;
		}
		
	}
	
	
	//ThreadForm画面を表示
	@GetMapping("/toThreadForm/{genreId}")
	public ModelAndView showThreadForm(@PathVariable(name = "genreId") int genreId,
			                           ModelAndView mv, Model model) {
		
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("threadData")) {
			mv.addObject("threadData", new ThreadData());
		}
		//genre(エンティティオブジェクト)を取得しViewへ渡す
		Genre genre;
		try {
			genre = genreRepository.findById(genreId).get();
			
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		mv.addObject("genre", genre);
		
		mv.setViewName("ThreadForm");
		return mv;
	}
	
	//スレッドを削除
	@Transactional
	@GetMapping("/deleteThread/{genreId}/{threadId}")
	public ModelAndView deleteThread(@PathVariable(name = "threadId")long threadId,
			                   @PathVariable(name = "genreId")Integer genreId,
			                   @RequestParam(name = "page", required = false)Integer page,
			                   @RequestParam(name = "bySearch", required = false)String bySearch,
	                           @RequestParam(name = "orderBy", required = false)String orderBy,
			                   ModelAndView mv) {
		//threadを削除
		//treadに関連したentityがあれば同時に削除する
		replyRepository.deleteReplyByThreadId(threadId);
		//画像を削除
		try {
			awsService.deleteImageFromS3ByThreadId(threadId);
		}catch(AmazonServiceException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//ローカル環境で使用
		//imageService.deleteImageByThreadId(threadId);
		imageRepository.deleteByThreadId(threadId);
		postingRepository.deletePostingByThreadId(threadId);
		threadRepository.deleteById(threadId);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = threadRepository.count_findByGenreId(genreId);
		//削除後にページに表示するthreadが無くなってしまう時は前のページへ移動する
		//現在のpageから1ページ分マイナスし、pageをViewへ渡す
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		mv = bbService.createViewInfoAboutThread(mv, page, total, limit, bySearch, orderBy);
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = createThreadList(genreId, page, orderBy);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	
	//Threadを投稿日時の新しい順or古い順で取得
	private List<Thread> createThreadList(Integer genreId, Integer page, String orderBy){
		if(orderBy.equals("orderByCreatedTime")) {
			List<Thread> threadList = bulletinboardDaoImpl.findByGenreIdOrderbyDesc(genreId, page);
			return threadList;
		}else if(orderBy.equals("orderByNumberOfPosting")){
			List<Thread> threadList = bulletinboardDaoImpl.findByGenreIdOrderbyNumberOfPosting(genreId, page);
			return threadList;
		}else {
			List<Thread> threadList = bulletinboardDaoImpl.findByGenreId(genreId, page);
			return threadList;
		}
	}
	
	//検索条件に一致したThreadの中から削除
	@Transactional
	@GetMapping("/deleteSearchedThread/{genreId}/{threadId}")
	public ModelAndView deleteSearchedThread(@PathVariable(name = "threadId")long threadId,
			                   @PathVariable(name = "genreId")Integer genreId,
			                   @RequestParam(name = "page", required = false)Integer page,
	                           @RequestParam(name = "bySearch", required = false)String bySearch,
	                           @RequestParam(name = "orderBy", required = false)String orderBy,
	                           @RequestParam(name = "threadTitle", required = false)String threadTitle,
			                   ModelAndView mv) {
		//threadを削除する
		//threadに関連付けられたentityがあれば同時に削除する
		replyRepository.deleteReplyByThreadId(threadId);
		//画像を削除
		try {
			awsService.deleteImageFromS3ByThreadId(threadId);
		}catch(AmazonServiceException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//ローカル環境で使用
		//imageService.deleteImageByThreadId(threadId);
		imageRepository.deleteByThreadId(threadId);
		postingRepository.deletePostingByThreadId(threadId);
		threadRepository.deleteById(threadId);
		
		//formから取得した文字列からキーワードを生成
		List<String> threadTitleList = bbService.toSearchWords(threadTitle);
		String searchWords = null;
		try {
			searchWords = Utils.toJson(threadTitleList);
		}catch(IOException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		//キーワードをViewへ渡す(ハイライト表示する時にパラメータとして必要な為)
		mv.addObject("searchWords", searchWords);
		mv.addObject("threadTitle", threadTitle);
		
		//Nullチェック後、ページング・並び替えに必要なパラメータをViewへ渡す
		if(page == null) {page = 0;}
		if(orderBy == null) {orderBy = "";}
		if(bySearch == null) {bySearch = "";}
		int total = threadRepository.count_findByGenreId(genreId);
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		mv = bbService.createViewInfoAboutThread(mv, page, total, limit, bySearch, orderBy);
		
		//genre(エンティティオブジェクト), threadListを取得しViewへ渡す
		Genre genre = genreRepository.findById(genreId).get();
		mv.addObject("genre", genre);
		List<Thread> threadList = createThreadList(genreId, page, orderBy, threadTitleList);
		mv.addObject("threadList", threadList);
		
		mv.setViewName("fragments/ThreadFragment :: threadFragment");
		return mv;
	}
	
	//キーワードに一致したthreadを投稿日時の新しい順or古い順で取得
	private List<Thread> createThreadList(Integer genreId, Integer page, String orderBy, List<String> threadTitleList){
		if(orderBy.equals("orderByCreatedTime")) {
			List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitleOrderbyDesc(threadTitleList, genreId, page);
			return threadList;
		}else if(orderBy.equals("orderByNumberOfPosting")){
			List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitleOrderbyNumberOfPosting(threadTitleList, genreId, page);
			return threadList;
		}else {
			List<Thread> threadList = bulletinboardDaoImpl.findByThreadTitle(threadTitleList, genreId, page);
			return threadList;
		}
	}
	

}
















