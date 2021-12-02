package com.example.bulletinboard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.bulletinboard.service.AwsService;

import lombok.RequiredArgsConstructor;

/**
 * アップロードされた画像のパスを取得するController
 * 
 */
@RestController
@RequiredArgsConstructor
public class DownloadController {
	
	//ローカル環境で使用
	//private final ImageService imageService;
	private final AwsService awsService;
	
	//アップロードされた画像のパスを取得する
	@GetMapping("/download/{postingId}")
	@ResponseBody
	public String download(@PathVariable Long postingId) {
		//ローカル環境で使用
		//String downloadImagePath = imageService.downloadImage(postingId);
		String downloadImagePath = awsService.downloadImageFromS3(postingId);
		
		return downloadImagePath;
		
	}
	

}
