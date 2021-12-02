package com.example.bulletinboard.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.bulletinboard.entity.Image;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {
	
	//画像ファイルのパスを作成
	public static String makeImageFilePath(String path, Image img) {
		return path + "/" + img.getCreatedTime() + "_" + img.getImageName();
	}
	
	//S3バケットのキー名を作成
	public static String makeS3KeyName(Image img) {
		return "images/" + img.getCreatedTime() + "_" + img.getImageName();
	}
	
	//文字列のListをjson化する
	public static String toJson(List<String> list) throws IOException{
		
		Map<Integer, String> map = new HashMap<>();
		for(int i = 0; i < list.size(); i++) {
			map.put(i, list.get(i));
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String json = objectMapper.writeValueAsString(map);
		
		return json;
	}
	
	//Listの要素数によってLIKE句を動的に生成
	public static String makeMessageQueryString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if(list.size() == 1) {
			sb.append(" and message like :message1");
		}else if(list.size() > 1) {
			for(int i = 0; i < list.size(); i++) {
				if(i == 0) {sb.append(" and (message like :message" + (i + 1));}
				if(i != list.size() - 1 && i > 0) {sb.append(" or message like :message" + (i + 1));}
				if(i == list.size() - 1) {sb.append(" or message like :message" + (i + 1) + ")");}
			}
		}else {
			
		}
		
		return sb.toString();
	}
	
	//Listの要素数によってLIKE句を動的に生成
	public static String makeThreadTitleQueryString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		if(list.size() == 1) {
			sb.append(" and thread_title like :thread_title1");
		}else if(list.size() > 1) {
			for(int i = 0; i < list.size(); i++) {
				if(i == 0) {sb.append(" and (thread_title like :thread_title" + (i + 1));}
				if(i != list.size() - 1 && i > 0) {sb.append(" or thread_title like :thread_title" + (i + 1));}
				if(i == list.size() - 1) {sb.append(" or thread_title like :thread_title" + (i + 1) + ")");}
			}
		}else {
			
		}
		
		return sb.toString();
	}
	
	//現在時刻を取得しlong値で返す
	public static long createTime() throws ParseException {
		
		Date time = new Date();
		long ms = time.getTime();
		return ms;

	}
	
	//現在から三カ月前の日付を生成
	public static String createTimeThreeMonthsAgo() {
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -3);
		Date _date = calendar.getTime();
		
		return sdf.format(_date);
	}

}













