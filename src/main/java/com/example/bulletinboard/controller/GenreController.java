package com.example.bulletinboard.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.amazonaws.AmazonServiceException;
import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.form.GenreData;
import com.example.bulletinboard.repository.GenreRepository;
import com.example.bulletinboard.repository.ImageRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.repository.ReplyRepository;
import com.example.bulletinboard.repository.ThreadRepository;
import com.example.bulletinboard.service.AwsService;
import com.example.bulletinboard.validator.All;

import lombok.RequiredArgsConstructor;

/**
 *Genreの表示、作成、削除に関する処理をするController
 *
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/genre")
public class GenreController {
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bulletinboardDaoImpl;
	@PostConstruct
	public void init() {
		bulletinboardDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	@Autowired
	HttpSession session;
	
	private final GenreRepository genreRepository;
	private final ThreadRepository threadRepository;
	private final PostingRepository postingRepository;
	private final ReplyRepository replyRepository;
	private final ImageRepository imageRepository;
	//private final ImageService imageService;
	private final AwsService awsService;
	
	//ジャンルの一覧表示
	@GetMapping("/showGenre")
	public ModelAndView showGenre(ModelAndView mv, Model model) {
		
		List<Genre> genreList = genreRepository.findAll();
		mv.addObject("genreList", genreList);
		mv.setViewName("Genre");
		return mv;
	}
	
	//GenreForm画面を表示
    @GetMapping("/toGenreForm")
	public String showGenreForm(@ModelAttribute GenreData genreData) {
		return "GenreForm";
	}
    
    //ジャンルの追加
    @Transactional
  	@PostMapping("/create")
  	public ModelAndView genreCreate(@ModelAttribute(name = "genreData")@Validated(All.class) GenreData genreData,
  			                        BindingResult result, ModelAndView mv) {
  		if(!result.hasErrors()) {
  			//エラーなし
  			Genre genre = genreData.toEntity();
  			genreRepository.saveAndFlush(genre);
  			mv.setViewName("redirect:/genre/showGenre");
  			return mv;
  			
  		}else {
  			//エラーあり
  			mv.setViewName("GenreForm");
  			return mv;
  			
  		}
  		
  	}
	
  	//削除処理
    @Transactional
  	@GetMapping("/delete/{genreId}")
  	public ModelAndView deleteGenreByAjax(@PathVariable(name = "genreId")Integer genreId, ModelAndView mv) {
    	
    	//Genreエンティティとそれに関連するエンティティを削除する
  		replyRepository.deleteReplyByGenreId(genreId);
  		//画像を削除
  		try {
  			awsService.deleteImageFromS3ByGenreId(genreId);
  			
  		}catch(AmazonServiceException e) {
  			e.printStackTrace();
  			mv.setViewName("error");
  			return mv;
  		}
  		//ローカル環境で使用
  		//imageService.deleteImageByGenreId(genreId);
		imageRepository.deleteByGenreId(genreId);
  		postingRepository.deletePostingByGenreId(genreId);
  		threadRepository.deleteThreadByGenreId(genreId);
  		genreRepository.deleteById(genreId);
  		
  		//genreListを取得しViewに渡す
		List<Genre> genreList = genreRepository.findAll();
		mv.addObject("genreList", genreList);
		
		mv.setViewName("fragments/GenreFragment :: genreFragment");
  		
  		return mv;
  		
  	}
  	
  	

}












