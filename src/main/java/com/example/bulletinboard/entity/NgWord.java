package com.example.bulletinboard.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * NGワードに関するEntity
 *
 */
@Entity
@Table(name = "ng_word")
@Data
public class NgWord {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ng_word_id")
	private Integer ngWordId;
	
	@Column(name = "ng_word")
	private String ngWord;
	

}
