package com.example.bulletinboard.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Inquiry;
import com.example.bulletinboard.entity.NgWord;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.form.SearchByPostingData;
import com.example.bulletinboard.form.SearchByReplyData;
import com.example.bulletinboard.model.PageInfo;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.InquiryRepository;
import com.example.bulletinboard.repository.NgWordRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.repository.UserInfoRepository;
import com.example.bulletinboard.service.PaginationService;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.Limit;
import com.example.bulletinboard.validator.Validator;

import lombok.RequiredArgsConstructor;

/**
 * 管理画面に関する処理をするController
 *
 */
@RequiredArgsConstructor
@Validated
@Controller
public class ManagementController {
	
	private final NgWordRepository ngWordRepository;
	private final GenreRepository genreRepository;
	private final ThreadRepository threadRepository;
	private final PostingRepository postingRepository;
	private final PaginationService paginationService;
	private final ValidationService validationService;
	private final UserInfoRepository userInfoRepository;
	private final InquiryRepository inquiryRepository;
	private final Validator validator;
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bbDaoImpl;
	@PostConstruct
	public void init() {
		bbDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	//1ページ当たりに表示するuserinfoの数を設定
	private int limit = Limit.USERINFO_LIMIT.getLimit();
	
	//管理画面を表示
	@GetMapping("/toManagement")
	public ModelAndView toManagement(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("error")) {
			mv.addObject("searchByPostingData", new SearchByPostingData());
			mv.addObject("searchByReplyData", new SearchByReplyData());
		}
		
		//Viewの表示に必要なパラーメータを用意し渡す
		PageInfo pageInfoAboutUserInfo = new PageInfo(0, 0, 0, 0, false, false);
		PageInfo pageInfoAboutInquiry = new PageInfo(0, 0, 0, 0, false, false);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		mv.addObject("pageInfoAboutInquiry", pageInfoAboutInquiry);
		model.addAttribute("ngWordList", null);
		model.addAttribute("userInfoList", null);
		model.addAttribute("inquiryList", null);
		
		mv.setViewName("Management");
		
		return mv;
	}
	//検索関連画面のタブを表示
	@GetMapping("/toSearch")
	public ModelAndView toSearch(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("error")) {
			mv.addObject("searchByPostingData", new SearchByPostingData());
			mv.addObject("searchByReplyData", new SearchByReplyData());
		}
		mv.setViewName("fragments/Search :: search");
		
		return mv;
	}
	
	//NGワード関連のタブを表示
	@GetMapping("/toNgWord")
	public ModelAndView toNgWord(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		//ngWordListを取得しViewへ渡す
		List<NgWord> ngWordList = ngWordRepository.findAll();
		mv.addObject("ngWordList", ngWordList);
		
		//NGワード関連のタブを表示する為に必要なパラメータをViewに渡す
		mv.addObject("ngWord", "ngWord");
		
		mv.setViewName("fragments/NgWord :: ngWord");
		return mv;
	}
	
	//お問い合わせ一覧を表示
	@GetMapping("/toInquiry")
	public ModelAndView toInquiry(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//ページングに必要な値を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = inquiryRepository.countInquiry();
		PageInfo pageInfoAboutInquiry = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutInquiry", pageInfoAboutInquiry);
		
		//iquiryListを取得しViewへ渡す
		List<Inquiry> inquiryList = bbDaoImpl.findInquiry(page);
		mv.addObject("inquiryList", inquiryList);
		
		//お問い合わせのタブを表示するために必要な値をViewへ渡す
		mv.addObject("inquiry", "inquiry");
		
		mv.setViewName("fragments/Inquiry :: inquiry");
		return mv;
	}
	
	//会員情報を表示
	@GetMapping("/toUserInfo")
	public ModelAndView toUserInfo(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//ページングに必要な値を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = userInfoRepository.countUserInfo();
		PageInfo pageInfoAboutUserInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		
		//userInfoListを取得しViewへ渡す
		List<UserInfo> userInfoList = bbDaoImpl.findUserInfo(page);
		mv.addObject("userInfoList", userInfoList);
		
		//会員情報のタブを表示するために必要な値をViewへ渡す
		mv.addObject("userInfo", "userInfo");
		
		mv.setViewName("fragments/UserInfo :: userInfo");
		return mv;
	}
	
	//３カ月間書き込みがないユーザーを表示する(幽霊会員を表示)
	@GetMapping("/searchGhostUser")
	public ModelAndView searchGhostUser(@RequestParam(name = "page", required = false)Integer page,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = bbDaoImpl.countGhostUser(page);
		PageInfo pageInfoAboutUserInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		
		//userInfoListを取得しViewへ渡す
		List<UserInfo> userInfoList = bbDaoImpl.findGhostUser(page);
		mv.addObject("userInfoList", userInfoList);
		
		//会員情報のタブを表示するために必要なパラメータをViewへ渡す
		mv.addObject("userInfo", "userInfo");
		mv.addObject("GhostUser", "GhostUser");
		
		mv.setViewName("fragments/UserInfo :: userInfo");
		return mv;
	}

	//ユーザー名から会員を検索
	@PostMapping("/searchUsername")
	public ModelAndView searchUsername(@RequestParam(name = "page", required = false)Integer page,
			                           @RequestParam(name = "username")String username,
			                           ModelAndView mv, Authentication loginUser, Model model) {
		
		//ページングに必要な値を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = userInfoRepository.countUsername(username);
		PageInfo pageInfoAboutUserInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		
		//userInfoListを取得しViewへ渡す
		List<UserInfo> userInfoList = new ArrayList<>();
		UserInfo userInfo = userInfoRepository.findByUsername(username);
		userInfoList.add(userInfo);
		mv.addObject("userInfoList", userInfoList);
		
		//会員情報のタブを表示するために必要なパラメータをViewへ渡す
		mv.addObject("userInfo", "userInfo");
		
		//usernameをバリデーションし、エラーがあった場合はViewでエラーメッセージを表示する
		if(username == null || username.isEmpty() || !validationService.isValid(username)) {
			mv.addObject("isBlank", true);
		}
		
		mv.setViewName("fragments/UserInfo :: userInfo");
		return mv;
	}
	
	//ユーザー情報を削除
	@Transactional
	@GetMapping("/deleteUserInfo/{userId}")
	public ModelAndView deleteUserInfo(@RequestParam(name = "page", required = false)Integer page,
			                           @PathVariable(name = "userId")Integer userId,
			                           RedirectAttributes redirectAttributes, ModelAndView mv) {
		//userInfoを削除する
		userInfoRepository.deleteById(userId);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = userInfoRepository.countUserInfo();
		//削除後にページに表示するuserInfoが無くなってしまう時は前のページへ移動する
		//現在のpageから1ページ分マイナスし、pageをViewへ渡す
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		PageInfo pageInfoAboutUserInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		
		//userInfoListを取得しViewへ渡す
		List<UserInfo> userInfoList = bbDaoImpl.findUserInfo(page);
		mv.addObject("userInfoList", userInfoList);
		
		//会員情報のタブを表示するために必要なパラメータをViewへ渡す
		mv.addObject("userInfo", "userInfo");
		
		mv.setViewName("fragments/UserInfo :: userInfo");
		return mv;
	}
	
	//GhostUser(幽霊会員)を削除する
	@Transactional
	@GetMapping("/deleteGhostUser/{userId}")
	public ModelAndView deleteUserInfoWhenShowingGhostUser(@RequestParam(name = "page", required = false)Integer page,
			                           @PathVariable(name = "userId")Integer userId,
			                           RedirectAttributes redirectAttributes, ModelAndView mv) {
		//GhostUserを削除
		userInfoRepository.deleteById(userId);
		
		//ページングに必要な情報を取得しViewへ渡す
		if(page == null ) {page = 0;}
		int total = bbDaoImpl.countGhostUser(page);
		//削除後にページに表示するuserInfoが無くなってしまう時は前のページへ移動する
		//現在のpageから1ページ分マイナスし、pageをViewへ渡す
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		PageInfo pageInfoAboutUserInfo = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutUserInfo", pageInfoAboutUserInfo);
		
		//UserInfoListを取得しViewへ渡す
		List<UserInfo> userInfoList = bbDaoImpl.findGhostUser(page);
		mv.addObject("userInfoList", userInfoList);
		
		//会員情報のタブを表示するために必要なパラメータをViewへ渡す
		mv.addObject("userInfo", "userInfo");
		mv.addObject("GhostUser", "GhostUser");
		
		mv.setViewName("fragments/UserInfo :: userInfo");
		return mv;
	}
	
	//検索条件に一致したPostingを表示する
	@PostMapping("/searchByPosting")
	public String searchByPosting(@ModelAttribute@Validated SearchByPostingData searchByPostingData,
			                      BindingResult result, RedirectAttributes redirectAttributes) {
		
		try {
			//エラーなし
			if(!result.hasErrors()) {
				//formオブジェクトから値を取得
				String genreTitle = searchByPostingData.getGenreTitle();
				String threadTitle = searchByPostingData.getThreadTitle();
				String postingNo = searchByPostingData.getPostingNo();
				
				//Postingを検索
				Long threadId = threadRepository.findThreadIdByTitle(genreTitle, threadTitle);
				if(threadId == null) {throw new NoResultException();}
				Posting posting = bbDaoImpl.findByPostingData(threadId, postingNo);
				
				//検索されたReplyを赤線で囲んで表示するために必要なパラメータをリダイレクト先に渡す
				redirectAttributes.addFlashAttribute("postingNoBySearch", Long.parseLong(searchByPostingData.getPostingNo()));
				return "redirect:/posting/showPosting/" + posting.getThreadId();
				
			}else {
				//エラーあり
				//エラーメッセージを表示するために必要なパラメータを取得しリダイレクト先に渡す
				redirectAttributes.addFlashAttribute("error", "error");
				redirectAttributes.addFlashAttribute("collapse", 1);
				redirectAttributes.addFlashAttribute("searchByPostingData", searchByPostingData);
				redirectAttributes.addFlashAttribute("searchByReplyData", new SearchByReplyData());
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.searchByPostingData", result);
				return "redirect:/toManagement";
			}
			
		}catch (NoResultException e) {
			//検索条件に一致するPostingが見つからなかったとき
			//リダイレクト先でメッセージを表示する
			redirectAttributes.addFlashAttribute("postingNotFound", "postingNotFound");
			redirectAttributes.addFlashAttribute("collapse", 1);
			return "redirect:/toManagement";
		}
		
	}
	//検索条件に一致したReplyを表示する
	@PostMapping("/searchByReply")
	public String searchByReply(@ModelAttribute@Validated SearchByReplyData searchByReplyData,
			                    BindingResult result, RedirectAttributes redirectAttributes) {
		
		try {
			if(!result.hasErrors()) {
				//エラーなし
				//formオブジェクトから値を取得
				String genreTitle = searchByReplyData.getGenreTitle();
				String threadTitle = searchByReplyData.getThreadTitle();
				String postingNo = searchByReplyData.getPostingNo();
				String replyNo = searchByReplyData.getReplyNo();
				
				//Replyを検索
				Integer genreId = genreRepository.findByGenreTitle(genreTitle).getGenreId();
				Long postingId = postingRepository.findPostingIdByIdAndTitleAndNo(genreId, threadTitle, Long.valueOf(postingNo));
				Reply reply = bbDaoImpl.findByReplyData(postingId, replyNo);
				
				//検索されたReplyを赤線で囲んで表示するために必要なパラメータをリダイレクト先に渡す
				redirectAttributes.addFlashAttribute("replyNoBySearch", Integer.parseInt(replyNo));
				return "redirect:/reply/showReply/" + reply.getPostingId();
				
			}else {
				//エラーあり
				//エラーメッセージを表示するために必要なパラメータを取得しリダイレクト先に渡す
				redirectAttributes.addFlashAttribute("error", "error");
				redirectAttributes.addFlashAttribute("collapse", 2);
				redirectAttributes.addFlashAttribute("searchByPostingData", new SearchByPostingData());
				redirectAttributes.addFlashAttribute("searchByReplyData", searchByReplyData);
				redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.searchByReplyData", result);
				return "redirect:/toManagement";
			}
			
		}catch (NoResultException e) {
			//検索条件に一致するPostingが見つからなかったとき
			//リダイレクト先でメッセージを表示する
			redirectAttributes.addFlashAttribute("collapse", 2);
			redirectAttributes.addFlashAttribute("replyNotFound", "replyNotFound");
			return "redirect:/toManagement";
		}
		
	}
	//Ngワードを登録
	@Transactional
	@PostMapping("/registerNgWord")
	public ModelAndView registerNgWord(@RequestParam(name = "ngWord")String ngWord,
			                     ModelAndView mv) {
		NgWord ng_word = new NgWord();
		//NgWordのバリデーション
		if(ngWord == null || ngWord.isEmpty() || !validationService.isValid(ngWord) || !validator.sizeIsValid(ngWord, 10)) {
			//エラーあり
			//NgWordListを取得しViewへ渡す
			List<NgWord> ngWordList = ngWordRepository.findAll();
			mv.addObject("ngWordList", ngWordList);
			
			//NgWord関連のタブを表示するために必要なパラメータを渡す
			mv.addObject("ngWord", "ngWord");
			mv.addObject("ngWordError", "ngWordError");
			
			mv.setViewName("fragments/NgWord :: ngWord");
			return mv;
		}else {
			//エラーなし
			//NgWordを登録
			ng_word.setNgWord(ngWord);
			ngWordRepository.saveAndFlush(ng_word);
			
			//NgWordListを取得しViewへ渡す
			List<NgWord> ngWordList = ngWordRepository.findAll();
			mv.addObject("ngWordList", ngWordList);
			
			//NgWord関連のタブを表示するためにひつようなパラメータを渡す
			mv.addObject("ngWord", "ngWord");
			
			mv.setViewName("fragments/NgWord :: ngWord");
			return mv;
		}
		
	}
	//Ngワードを削除する
	@Transactional
	@GetMapping("/deleteNgWord/{ngWordId}")
	public ModelAndView deleteNgWord(@PathVariable(name = "ngWordId")Integer ngWordId,
			                   ModelAndView mv) {
		//NgWordを削除する
		NgWord ng_word = ngWordRepository.findById(ngWordId).get();
		ngWordRepository.delete(ng_word);
		
		//NgWordListを取得しViewへ渡す
		List<NgWord> ngWordList = ngWordRepository.findAll();
		mv.addObject("ngWordList", ngWordList);
		
		//NgWord関連のタブを表示するためにひつようなパラメータを渡す
		mv.addObject("ngWord", "ngWord");
		
		mv.setViewName("fragments/NgWord :: ngWord");
		return mv;
	}
	
}





















