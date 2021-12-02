DROP DATABASE IF EXISTS TEST;
CREATE DATABASE TEST;
USE TEST;

DROP TABLE IF EXISTS genre;
CREATE TABLE genre(
  genre_id INT PRIMARY KEY AUTO_INCREMENT,
  genre_title VARCHAR(255)
);

DROP TABLE IF EXISTS thread;
CREATE TABLE thread(
  thread_id INT PRIMARY KEY AUTO_INCREMENT,
  thread_title VARCHAR(255),
  created_time VARCHAR(255),
  number_of_posting INT,
  genre_id INT,
  username VARCHAR(255),
  thread_version INT
);

DROP TABLE IF EXISTS posting;
CREATE TABLE posting(
  posting_id INT PRIMARY KEY AUTO_INCREMENT,
  posting_no INT,
  name VARCHAR(255),
  writing_time VARCHAR(255),
  number_of_reply INT,
  message VARCHAR(255),
  thread_id INT,
  genre_id INT,
  username VARCHAR(255),
  posting_version INT,
  has_image INT
);

DROP TABLE IF EXISTS reply;
CREATE TABLE reply(
  reply_id INT PRIMARY KEY AUTO_INCREMENT,
  reply_no INT,
  name VARCHAR(255),
  reply_time VARCHAR(255),
  reply_message VARCHAR(255),
  posting_id INT,
  thread_id INT,
  genre_id INT,
  username VARCHAR(255)
);

DROP TABLE IF EXISTS ng_word;
CREATE TABLE ng_word(
  ng_word_id INT PRIMARY KEY AUTO_INCREMENT,
  ng_word VARCHAR(255)
);

DROP TABLE IF EXISTS user_info;
CREATE TABLE user_info(
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255),
  password VARCHAR(255),
  gender VARCHAR(255),
  role VARCHAR(255),
  last_writing_time DATE
);

DROP TABLE IF EXISTS image;
CREATE TABLE image(
  image_id INT PRIMARY KEY AUTO_INCREMENT,
  posting_id INT,
  image_name VARCHAR(255),
  created_time VARCHAR(255),
  thread_id INT,
  genre_id INT
);

DROP TABLE IF EXISTS inquiry;
CREATE TABLE inquiry(
  inquiry_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(255),
  message VARCHAR(255),
  inquiry_time VARCHAR(255),
  user_id INT
);





