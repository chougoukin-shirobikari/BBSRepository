package com.example.bulletinboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * Postingに投稿した画像の情報に関するEntity
 *
 */
@Entity
@Table(name = "image")
@Data
public class Image {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Integer imageId;
	
	@Column(name = "posting_id")
	private Long postingId;
	
	@Column(name = "image_name")
	private String imageName;
	
	@Column(name = "created_time")
	private String createdTime;
	
	@Column(name = "thread_id")
	private Long threadId;
	
	@Column(name = "genre_id")
	private Integer genreId;
	

}
