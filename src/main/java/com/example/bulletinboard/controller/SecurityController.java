package com.example.bulletinboard.controller;

import java.sql.Date;
import java.text.ParseException;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.bulletinboard.dao.BulletinBoardDaoImpl;
import com.example.bulletinboard.entity.UserInfo;
import com.example.bulletinboard.form.UserInfoData;
import com.example.bulletinboard.repository.UserInfoRepository;
import com.example.bulletinboard.service.ValidationService;
import com.example.bulletinboard.util.Utils;

import lombok.RequiredArgsConstructor;

/**
 * セキュリティ関連の処理、設定に関するController
 *
 */
@RequiredArgsConstructor
@Controller
public class SecurityController {
	
	private final UserInfoRepository userInfoRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ValidationService validationService;
	
	@PersistenceContext
	private EntityManager entityManager;
	BulletinBoardDaoImpl bbDaoImpl;
	@PostConstruct
	public void init() {
		bbDaoImpl = new BulletinBoardDaoImpl(entityManager);
	}
	
	//"/"にアクセスがあったときToPageを表示
	@GetMapping("/")
	public String toTopPage() {
		return "TopPage";
	}
	
	//Topページを表示
	@GetMapping("/TopPage")
	public String showTopPage() {
		return "TopPage";
	}
	
	//ログイン画面を表示
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	//新規登録画面を表示する
	@GetMapping("/register")
	public ModelAndView register(ModelAndView mv, Model model) {
		//Modelにバリデーションのエラー情報が入っていない場合のみFormオブジェクトを生成する
		if(!model.containsAttribute("userInfoData")) {
			mv.addObject("userInfoData", new UserInfoData());
		}
		
		//ADMIN権限を持ったユーザーが存在するかチェック
		boolean adminExists = false;
		UserInfo admin = userInfoRepository.findByRole("ADMIN");
		if(admin != null) {adminExists = true; }
		mv.addObject("adminExists", adminExists);
		
		mv.setViewName("Register");
		return mv;
	}
	//新規登録する
	@Transactional
	@PostMapping("/register")
	public ModelAndView userRegist(@ModelAttribute UserInfoData userInfoData, BindingResult result,
			                       RedirectAttributes redirectAttributes, ModelAndView mv) {
		
		//username, pawwordをバリデーション
		String username = userInfoData.getUsername();
		String password = userInfoData.getPassword();
		validationService.usernameIsValid(username, "username", 2, 20, result);
		validationService.passwordIsValid(password, "password", 4, 255, result);
		
		if(!result.hasErrors()) {
			//エラーなし
			//登録に必要な情報を取得
			UserInfo userInfo = new UserInfo();
			userInfo.setUsername(userInfoData.getUsername());
			userInfo.setPassword(passwordEncoder.encode(userInfoData.getPassword()));
			
			//genderに値をセット
			int gender = userInfoData.getGender();
			if(gender == 0) {
				userInfo.setGender("男性");
			}else if(gender == 1) {
				userInfo.setGender("女性");
			}else {
				userInfo.setGender("その他");
			}
			
			//roleに値をセット
			if(userInfoData.isAdmin()) {
				userInfo.setRole("ADMIN");
				
			}else {
				userInfo.setRole("USER");
			}
			
			//userInfoに登録日時をセット
			try {
				userInfo.setLastWritingTime(new Date(Utils.createTime()));
				userInfoRepository.saveAndFlush(userInfo);
				//リダイレクト先で登録完了メッセージを表示
				redirectAttributes.addFlashAttribute("isSuccessful", "isSuccessful");
				mv.setViewName("redirect:/login");
				return mv;
				
			}catch(ParseException e) {
				//TODO
				//登録中にエラーがあった場合login画面へリダイレクト
				mv.setViewName("redirect:/login");
				return mv;
			}
			
		}else {
			//バリデーションでエラーあり
			//ADMIN権限を持つユーザーが存在するかチェック
			boolean adminExists = false;
			UserInfo admin = userInfoRepository.findByRole("ADMIN");
			//ADMIN権限を持つユーザが既に存在した場合はModelにエラー情報としてセット
			if(admin != null) {adminExists = true; }
			redirectAttributes.addFlashAttribute("adminExists", adminExists);
			
			//他のエラー情報をModelにセットしてリダイレクト
			redirectAttributes.addFlashAttribute("userInfoData", userInfoData);
  			redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userInfoData", result);
  			
  			mv.setViewName("redirect:/register");
  			return mv;
		}
		
	}
	

}





















