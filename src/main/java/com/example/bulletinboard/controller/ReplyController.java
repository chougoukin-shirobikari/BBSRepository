package com.example.bulletinboard.controller;

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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.entity.Reply;
import com.example.bulletinboard.form.ReplyData;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.service.BulletinboardService;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.MaxNumber;

import lombok.RequiredArgsConstructor;

/**
 * Replyの表示、作成、削除に関する処理をするController
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/reply")
public class ReplyController {
	
	private final PostingRepository postingRepository;
	private final ReplyRepository replyRepository;
	private final BulletinboardService bbService;
	private final ValidationService validationService;
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bulletinboardDaoImpl;
	@PostConstruct
	public void init() {
		bulletinboardDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	//1つのPostingに投稿できるReplyの数を設定
	private final int maxNumberOfReply = MaxNumber.OF_REPLY.getMaxNumber();
	
	//コメントの表示
	@GetMapping("/showReply/{postingId}")
	public ModelAndView showReply(@PathVariable(name = "postingId")long postingId, ModelAndView mv, Model model) {
		
		//replyList, Posting(エンティティオブジェクト)をViewへ渡す
		List<Reply> replyList = bulletinboardDaoImpl.findByPostingId(postingId);
		Posting posting = postingRepository.findById(postingId).get();
		mv.addObject("posting", posting);
		mv.addObject("replyList", replyList);
		//1つのPostingに投稿できるReplyの数をViewへ渡す
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		mv.setViewName("Reply");
		return mv;
	}
	
	//ReplyFormへ
	@GetMapping("/toReplyForm/{postingId}")
	public ModelAndView toReplyForm(@PathVariable(name = "postingId")long postingId,
			                        ModelAndView mv, Model model) {
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("replyData")) {
			mv.addObject("replyData", new ReplyData());
		}
		//Posting(エンティティオブジェクト)をViewへ渡す
		Posting posting;
		try {
			posting = postingRepository.findById(postingId).get();
		}catch(NoSuchElementException e) {
			e.printStackTrace();
			mv.setViewName("error");
			return mv;
		}
		mv.addObject("posting", posting);
		//投稿制限に達したPostingにコメントを投稿しようとした場合(URLの直接入力等で)リダイレクト
		if(posting.getNumberOfReply() >= maxNumberOfReply) {
			mv.setViewName("redirect:/reply/showReply/" + posting.getPostingId());
			return mv;
		}
		
		mv.setViewName("ReplyForm");
		return mv;
	}
	
	//返信を作成
	@PostMapping("/create/{postingId}")
	synchronized public String createReply(@PathVariable(name = "postingId")long postingId,
			                  @RequestParam(name = "version")long postingVersion,
			                  @ModelAttribute ReplyData replyData,
			                  BindingResult result, RedirectAttributes redirectAttributes) {
		
		//name, replyMessageをバリデーション
		String name = replyData.getName();
		String replyMessage = replyData.getReplyMessage();
		validationService.nameIsValid(name, 12, result);
		validationService.messageIsValid(replyMessage, "replyMessage", 60, result);
		
		if(!result.hasErrors()) {
			//エラーなし
			//saveReplyメソッドに必要な引数を取得
			Posting posting = postingRepository.findById(postingId).get();
  			List<Reply> replyList = bulletinboardDaoImpl.findByPostingId(postingId);
  			int size = replyList.size();
  			
  			try {
  				//Replyを登録
  				bbService.saveReply(postingVersion, replyData, posting, size);
  				//Posting(エンティティオブジェクト)をリダイレクト先へ渡す
  	  			redirectAttributes.addFlashAttribute("posting", posting);
  	  			return "redirect:/reply/showReply/" + posting.getPostingId();
  				
  			}catch(OptimisticLockingFailureException | ParseException e) {
  				//Replyの登録中にエラーがあった場合
  				//エラーメッセージをリダイレクト先で表示
  				redirectAttributes.addFlashAttribute("OptimisticLockingFailure", true);
  				return "redirect:/reply/toReplyForm/" + postingId;
  			}
  			
		}else {
			//バリデーションでエラーあり
			//リダイレクト先でエラーメッセージを表示
			redirectAttributes.addFlashAttribute("replyData", replyData);
  			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.replyData", result);
  			return "redirect:/reply/toReplyForm/" + postingId;
			
		}
		
	}
	
	//コメントを削除("削除されました"と表示し、データベースからは削除しない)
	@Transactional
	@GetMapping("/update/{postingId}/{replyId}")
	public ModelAndView updateReplyByAjax(@PathVariable(name = "postingId")long postingId,
			                  @PathVariable(name = "replyId")Integer replyId, ModelAndView mv) {
		
		//update(更新処理)するReplyを取得
		Reply reply = replyRepository.findById(replyId).get();
		reply.setName("削除済み");
		reply.setReplyTime("削除済み");
		reply.setReplyMessage("この返信は削除されました");
		replyRepository.saveAndFlush(reply);
		
		//replyList, posting(エンティティオブジェクト)をViewへ渡す
		List<Reply> replyList = bulletinboardDaoImpl.findByPostingId(postingId);
		Posting posting = postingRepository.findById(postingId).get();
		mv.addObject("posting", posting);
		mv.addObject("replyList", replyList);
		
		//1つのPostingに投稿できるReplyの数をViewへ渡す
		mv.addObject("maxNumberOfReply", maxNumberOfReply);
		
		mv.setViewName("fragments/ReplyFragment :: replyFragment");
		return mv;
		
	}
	
}














