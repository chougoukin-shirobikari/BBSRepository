package com.example.bulletinboard.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Inquiry;
import com.example.bulletinboard.form.InquiryData;
import com.example.bulletinboard.model.PageInfo;
import com.example.bulletinboard.repository.InquiryRepository;
import com.example.bulletinboard.repository.UserInfoRepository;
import com.example.bulletinboard.service.PaginationService;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.Limit;
import com.example.bulletinboard.util.Mail;

import lombok.RequiredArgsConstructor;

/**
 * お問い合わせに関する処理をするController
 * 
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/inquiry")
public class InquiryController {
	
	private final UserInfoRepository userInfoRepository;
	private final InquiryRepository inquiryRepository;
	private final ValidationService validationService;
	private final PaginationService paginationService;
	private final Mail mail;
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bbDaoImpl;
	@PostConstruct
	public void init() {
		bbDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	private int limit = Limit.INQUIRY_LIMIT.getLimit();
	
	//InquiryForm画面へ
	@GetMapping("/toInquiryForm")
	public String toInquiryForm(@ModelAttribute InquiryData inquiryData) {
		return "InquiryForm";
	}
	
	//問い合わせをする
	@Transactional
	@PostMapping("/makeAnInquiry")
	public String makeAnInquiry(@ModelAttribute InquiryData inquiryData, BindingResult result,
			                     RedirectAttributes redirectAttributes) {
		//usernameとmessageをバリデーション
		String username = inquiryData.getUsername();
		String message = inquiryData.getMessage();
		validationService.usernameInFormIsValid(username, "username", 12, result);
		validationService.messageInFormIsValid(message, "message", 60, result);
		
		if(!result.hasErrors()) {
			//エラーなし
			Inquiry inquiry = inquiryData.toInquiry();
			Integer userId = userInfoRepository.findByUsername(inquiryData.getUsername()).getUserId();
			inquiry.setUserId(userId);
			inquiryRepository.saveAndFlush(inquiry);
			//管理人のg-mailアドレスにお問い合わせがあったことを通知する
			mail.sendMailAboutInquiry(username);
			//リダイレクト先でメッセージを表示する為にパラメータを渡す
			redirectAttributes.addFlashAttribute("isSuccessful", "isSuccessful");
			return "redirect:/inquiry/toInquiryForm";
		}else {
			//エラーあり
			return "InquiryForm";
		}
	}
	
	//お問い合わせを削除する
	@Transactional
	@GetMapping("/deleteInquiry/{inquiryId}")
	public ModelAndView deleteInquiry(@RequestParam(name = "page", required = false)Integer page,
			                           @PathVariable(name = "inquiryId", required = false)Long inquiryId,
			                           RedirectAttributes redirectAttributes, ModelAndView mv) {
		//お問い合わせを削除する
		inquiryRepository.deleteById(inquiryId);
		
		//ページングに必要な値を取得しViewに渡す
		if(page == null ) {page = 0;}
		int total = inquiryRepository.countInquiry();
		if(total != 0 && total % limit == 0) {
			page = page - 1;
		}
		PageInfo pageInfoAboutInquiry = paginationService.createPageInfo(page, total, limit);
		mv.addObject("pageInfoAboutInquiry", pageInfoAboutInquiry);
		
		//inquiryListを取得しViewに渡す
		List<Inquiry> inquiryList = bbDaoImpl.findInquiry(page);
		mv.addObject("inquiryList", inquiryList);
		
		//タブでinquiryを表示する為にパラメータを渡す
		mv.addObject("inquiry", "inquiry");
		
		mv.setViewName("fragments/Inquiry :: inquiry");
		
		return mv;
	}

}













