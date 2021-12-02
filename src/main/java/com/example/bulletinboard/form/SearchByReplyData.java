package com.example.bulletinboard.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理画面のReply検索で入力された情報を保持するフォームクラス
 *
 */
@NoArgsConstructor
@Data
public class SearchByReplyData {
	
	@NotBlank(message = "未入力")
	private String genreTitle;
	
	@NotBlank(message = "未入力")
	private String threadTitle;
	
	@Pattern(regexp = "^[0-9]+$", message = "半角の数字を入力してください")
	private String postingNo;
	
	@Pattern(regexp = "^[0-9]+$", message = "半角の数字を入力してください")
	private String replyNo;
	

}
