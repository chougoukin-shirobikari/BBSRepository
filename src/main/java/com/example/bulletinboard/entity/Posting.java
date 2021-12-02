package com.example.bulletinboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.Data;

@Entity
@Table(name = "posting")
@Data
public class Posting {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "posting_id")
	private Long postingId;
	
	@Column(name = "posting_no")
	private long postingNo;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "writing_time")
	private String writingTime;
	
	@Column(name = "number_of_reply")
	private Integer numberOfReply;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "thread_id")
	private Long threadId;
	
	@Column(name = "genre_id")
	private Integer genreId;
	
	@Column(name = "username")
	private String username;
	
	@Version
	@Column(name = "posting_version")
	private long postingVersion;
	
	@Column(name = "has_image")
	private Integer hasImage;

}





