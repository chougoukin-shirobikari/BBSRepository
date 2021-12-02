package com.example.bulletinboard.service;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.bulletinboard.entity.Image;
import com.example.bulletinboard.entity.Posting;
import com.example.bulletinboard.repository.ImageRepository;
import com.example.bulletinboard.repository.PostingRepository;
import com.example.bulletinboard.util.Utils;

import lombok.RequiredArgsConstructor;

/**
 * 画像の処理に関するサービスクラス
 *
 */
@Service
@RequiredArgsConstructor
public class ImageService {
	
	private final ImageRepository imageRepository;
	private final PostingRepository postingRepository;
	
	//アップロードした画像を一時的に保存するディレクトリのパス
	@Value("${image.file.path}")
	private String IMAGE_FILE_PATH;
	//Linuxのホームディレクトリのパス
	@Value("${home.directory.path}")
	private String HOME_DIRECTORY_PATH;
	
	//MultipartFileが空でないか、画像ファイルであるかをチェック
	@Transactional
	public boolean doUploadingImage(MultipartFile file) throws IOException {
		
		if(file.isEmpty()) {
			return false;
			
		}else if(!file.isEmpty() && isImageFile(file)){
			return true;
		
		}else {
			throw new IOException();
		}

	}
	
	//MultipartFileが画像ファイルであるかをチェック
	public boolean isImageFile(MultipartFile multipartFile) throws IOException { 
		
		File file = new File(multipartFile.getOriginalFilename());
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(multipartFile.getBytes());
		fos.close();
		
		if(file != null && file.isFile()) {
			BufferedImage bi = ImageIO.read(file);
			if(bi != null) {
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
		
	}
	
	//MultipartFileを保存
	public void saveImageFile(Long postingId, MultipartFile file) throws IOException {
		
		//MultipartFileの名前を取得
		String imageName = file.getOriginalFilename();
		
		//一時的に画像を保存するディレクトリがない場合は作成
		File uploadDir = new File(IMAGE_FILE_PATH);
		if(!uploadDir.exists()) {
			uploadDir.mkdirs();
		}
		
		//投稿時刻を取得
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String createdTime = sdf.format(new Date());
		
		//画像を投稿するpostingを取得
		Posting posting = postingRepository.findById(postingId).get();
		//Imageエンティティに値をセット
		Image img = new Image();
		img.setPostingId(postingId);
		img.setImageName(imageName);
		img.setCreatedTime(createdTime);
		img.setThreadId(posting.getThreadId());
		img.setGenreId(posting.getGenreId());
		
		//fileに書き込み
		byte[] contents;
		try(BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(Utils.makeImageFilePath(IMAGE_FILE_PATH, img)))){
			contents = file.getBytes();
			bos.write(contents);
			imageRepository.saveAndFlush(img);
			
			//Linuxのホームディレクトリに複製されてしまう画像を削除
			File deleteFile = new File(HOME_DIRECTORY_PATH + img.getImageName());
			deleteFile.delete();
			
		}catch(IOException e) {
			e.printStackTrace();
			throw new IOException();
		}
	}
	
	//画像ファイルのパスを取得
	public String downloadImage(Long postingId) {
		Image img = imageRepository.findByPostingId(postingId);
		String downloadImagePath = "/images/" + img.getCreatedTime() + "_" + img.getImageName();
		return downloadImagePath;
	}
	
	//画像ファイルをposting_idで削除
	public void deleteImageByPostingId(Long postingId) {
		Image img = imageRepository.findByPostingId(postingId);
		File file = new File(Utils.makeImageFilePath(IMAGE_FILE_PATH, img));
		file.delete();
	}
	
	//画像ファイルをthread_idで削除
	public void deleteImageByThreadId(Long threadId) {
		//画像ファイルのListを取得
		List<Image> imageList = imageRepository.findByThreadId(threadId);
		if(imageList.size() > 0) {
			//Listに含まれる画像ファイルを取り出して削除
			for(Image image : imageList) {
				File file = new File(Utils.makeImageFilePath(IMAGE_FILE_PATH, image));
				file.delete();
			}
			
		}
	}
	
	//画像ファイルをgenre_idで削除
	public void deleteImageByGenreId(Integer genreId) {
		//画像ファイルのListを取得
		List<Image> imageList = imageRepository.findByGenreId(genreId);
		if(imageList.size() > 0) {
			//Listに含まれる画像ファイルを取り出して削除
			for(Image image : imageList) {
				File file = new File(Utils.makeImageFilePath(IMAGE_FILE_PATH, image));
				file.delete();
			}
			
		}
		
	}

}


















