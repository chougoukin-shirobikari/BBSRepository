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
@Table(name = "thread")
@Data
public class Thread {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "thread_id")
	private Long threadId;
	
	@Column(name = "thread_title")
	private String threadTitle;
	
	@Column(name = "created_time")
	private String createdTime;
	
	@Column(name = "number_of_posting")
	private Integer numberOfPosting;
	
	@Column(name = "genre_id")
	private Integer genreId;
	
	@Column(name = "username")
	private String username;
	
	@Version
	@Column(name = "thread_version")
	private long threadVersion;
	

}

















