package com.example.bulletinboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "reply")
@Data
public class Reply {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reply_id")
	private Integer replyId;
	
	@Column(name = "reply_no")
	private Integer replyNo;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "reply_time")
	private String replyTime;
	
	@Column(name = "reply_message")
	private String replyMessage;
	
	@Column(name = "posting_id")
	private Long postingId;
	
	@Column(name = "thread_id")
	private Long threadId;
	
	@Column(name = "genre_id")
	private Integer genreId;
	
	@Column(name = "username")
	private String username;
	

}













