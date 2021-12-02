package com.example.bulletinboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bulletinboard.entity.NgWord;

@Repository
public interface NgWordRepository extends JpaRepository<NgWord, Integer> {

}
