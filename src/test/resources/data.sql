INSERT INTO genre VALUES(1, 'genre1');

INSERT INTO thread VALUES(1, 'thread1', 'null', 2, 1, 'user', 0);
INSERT INTO thread VALUES(2, 'thread2', 'null', 2, 1, 'user', 0);
INSERT INTO thread VALUES(3, 'thread3', 'null', 1, 1, 'user', 0);

INSERT INTO posting VALUES(1, 1, 'name1', 'null', 1, 'message1', 1, 1, 'user', 0, 1);
INSERT INTO posting VALUES(2, 2, 'name2', 'null', 1, 'message2', 1, 1, 'user', 0, null);
INSERT INTO posting VALUES(3, 3, 'name3', 'null', 1, 'message3', 1, 1, 'user', 0, null);
INSERT INTO posting VALUES(4, 1, 'name1', 'null', 0, 'message', 2, 1, 'user', 0, null);
INSERT INTO posting VALUES(5, 2, 'name2', 'null', 0, 'message', 2, 1, 'user', 0, null);
INSERT INTO posting VALUES(6, 1, 'name', 'null', 0, 'message', 3, 1, 'user', 0, null);


INSERT INTO reply VALUES(1, 1, 'name', 'null', 'message', 1, 1, 1, 'user');
INSERT INTO reply VALUES(2, 1, 'name', 'null', 'message', 2, 1, 1, 'user');

INSERT INTO image VALUES(1, 1, 'image.jpg', 'now', 1, 1);

INSERT INTO ng_word VALUES(1, 'ngword');

INSERT INTO user_info VALUES(1, 'testuser', 'password', '男性', 'USER', '2021-07-22');
INSERT INTO user_info VALUES(2, 'ghostuser', 'password', '男性', 'USER', '2021-07-22');

INSERT INTO inquiry VALUES(1, 'testuser', 'message', '2020-01-01', 1)

