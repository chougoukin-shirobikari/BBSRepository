package com.example.bulletinboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * お問い合わせに関するEntity
 *
 */
@Entity
@Table(name = "inquiry")
@Data
public class Inquiry {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "inquiry_id")
	private Long inquiryId;
	
	@Column(name = "username")
	private String username;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "inquiry_time")
	private String inquiryTime;
	
	@Column(name = "user_id")
	private Integer userId;
	

}





