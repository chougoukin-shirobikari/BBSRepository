package com.example.bulletinboard.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.bulletinboard.entity.Genre;
import com.example.bulletinboard.validator.First;
import com.example.bulletinboard.validator.GenreTitleIsUnique;
import com.example.bulletinboard.validator.NotFullwidthSpace;
import com.example.bulletinboard.validator.NotNgWord;
import com.example.bulletinboard.validator.Second;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GenreData {
	
	@NotBlank(message = "ジャンル名を入力してください", groups = First.class)
	@Size(max = 8, message = "８文字以内で入力してください", groups = Second.class)
	@NotNgWord(groups = First.class)
	@GenreTitleIsUnique(groups = First.class)
	@NotFullwidthSpace(groups = First.class)
	private String genreTitle;
	
	//入力データからGenreを生成して返す
	public Genre toEntity() {
		Genre genre = new Genre();
		genre.setGenreTitle(this.genreTitle);
		
		return genre;
	}
	

}
