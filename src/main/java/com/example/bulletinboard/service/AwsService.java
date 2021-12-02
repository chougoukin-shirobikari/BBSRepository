package com.example.bulletinboard.service;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.bulletinboard.entity.Image;
import com.example.bulletinboard.repository.ImageRepository;
import com.example.bulletinboard.util.Utils;

import lombok.RequiredArgsConstructor;

/**
 * AWS SDKの処理に関するサービスクラス
 *
 */
@Service
@RequiredArgsConstructor
public class AwsService {
	
	private final ImageRepository imageRepository;
	
	//アップロードした画像を一時的に保存するディレクトリのパス
	@Value("${image.file.path}")
	private String IMAGE_FILE_PATH;
	//アップロードした画像を保存するS3バケットのパス
	@Value("${s3.bucket.url}")
	private String S3_BUCKET_URL;
	
	AmazonS3 client = AmazonS3ClientBuilder
			.standard()
			.withRegion(Regions.AP_NORTHEAST_1)
			.build();
	
	
	//画像をS3にアップロードする
	public void uploadImageToS3(Long postingId) throws AmazonServiceException {
		
		Image img = imageRepository.findByPostingId(postingId);
		File file = new File(Utils.makeImageFilePath(IMAGE_FILE_PATH, img));
		client.putObject(
				"mybbsbucket",
				Utils.makeS3KeyName(img) ,
				file
				);
		file.delete();
	}
	
	//S3にアップロードした画像のパスを取得
	public String downloadImageFromS3(Long postingId) throws AmazonServiceException {
		Image img = imageRepository.findByPostingId(postingId);
		String downloadImagePath = S3_BUCKET_URL + img.getCreatedTime() + "_" + img.getImageName();
		return downloadImagePath;
	}
	
	//posting_idでS3にアップロードした画像を削除
	public void deleteImageFromS3ByPostingId(Long postingId) throws AmazonServiceException {
		Image img = imageRepository.findByPostingId(postingId);
		if(img != null) {
			client.deleteObject(
					"mybbsbucket",
					Utils.makeS3KeyName(img)
					);
		}
		
	}
	
	//thead_idでS3にアップロードした画像を削除
	public void deleteImageFromS3ByThreadId(Long threadId) {
		List<Image> imageList = imageRepository.findByThreadId(threadId);
		if(imageList.size() > 0) {
			for(Image img : imageList) {
				client.deleteObject(
						"mybbsbucket",
						Utils.makeS3KeyName(img)
						);
			}
			
		}
	}
	
	//genre_idでS3にアップロードした画像を削除
	public void deleteImageFromS3ByGenreId(Integer genreId) {
		List<Image> imageList = imageRepository.findByGenreId(genreId);
		if(imageList.size() > 0) {
			for(Image img : imageList) {
				client.deleteObject(
						"mybbsbucket",
						Utils.makeS3KeyName(img)
						);
			}
			
		}
		
	}

}














