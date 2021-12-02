package com.example.bulletinboard.util;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UtilsTest {
	
	@Test
	void Listに複数のmessageが含まれる時にLike句が動的に生成されることを期待します() throws Exception{
		//Arrange
		String expected =" and (message like :message1 or message like :message2 or message like :message3)";
		//Act
		List<String> list = new ArrayList<>();
		list.add("message1");
		list.add("message2");
		list.add("message3");
		String actual = Utils.makeMessageQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void Listに含まれるmessageが１つの時Like句が生成されることを期待します() throws Exception{
		//Arrange
		String expected =" and message like :message1";
		//Act
		List<String> list = new ArrayList<>();
		list.add("message1");
		String actual = Utils.makeMessageQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void Listにmessageが含まれないときLike句が生成されないことを期待します() throws Exception{
		//Arrange
		String expected ="";
		//Act
		List<String> list = new ArrayList<>();
		String actual = Utils.makeMessageQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void Listに複数のthreadTitleが含まれる時にLike句が動的に生成されることを期待します() throws Exception{
		//Arrange
		String expected =" and (thread_title like :thread_title1 or thread_title like :thread_title2 or thread_title like :thread_title3)";
		//Act
		List<String> list = new ArrayList<>();
		list.add("threadTitle1");
		list.add("threadTitle2");
		list.add("threadTitle3");
		String actual = Utils.makeThreadTitleQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void Listに含まれるthreadTitleが１つの時Like句が生成されることを期待します() throws Exception{
		//Arrange
		String expected =" and thread_title like :thread_title1";
		//Act
		List<String> list = new ArrayList<>();
		list.add("message1");
		String actual = Utils.makeThreadTitleQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
	
	@Test
	void ListにthreadTitleが含まれないときにLike句が生成されないことを期待します() throws Exception{
		//Arrange
		String expected ="";
		//Act
		List<String> list = new ArrayList<>();
		String actual = Utils.makeThreadTitleQueryString(list);
		//Assert
		assertThat(actual, is(expected));
	}
}













