package com.example.bulletinboard.service;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BulletinBoardServiceTest {
	
	@Autowired
	BulletinboardService bbService;
	
	@Test
	void アルファベットの文字列を半角or全角スペースで分割しListが生成されることを期待します() throws Exception{
		//Arrange
		List<String> expected = new ArrayList<>();
		expected.add("SearchWord1");
		expected.add("SearchWord2");
		expected.add("SearchWord3");
		//Act
		String SarchWords = "SearchWord1　SearchWord2 SearchWord3";
		List<String> actual = bbService.toSearchWords(SarchWords);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void 日本語の文字列を半角or全角スペースで分割しListが生成されることを期待します() throws Exception{
		//Arrange
		List<String> expected = new ArrayList<>();
		expected.add("検索ワード１");
		expected.add("検索ワード２");
		expected.add("検索ワード３");
		//Act
		String SarchWords = "　検索ワード１　検索ワード２ 検索ワード３ ";
		List<String> actual = bbService.toSearchWords(SarchWords);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void 文字列に半角or全角スペースが含まれていなかったときはそのままListに格納されることを期待します() throws Exception{
		//Arrange
		List<String> expected = new ArrayList<>();
		expected.add("検索ワード");
		//Act
		String SarchWords = "検索ワード";
		List<String> actual = bbService.toSearchWords(SarchWords);
		//Assert
		assertThat(actual, is(expected));
	}
	
}













