INSERT INTO reply VALUES(3, 2, 'name2', 'null', 'message', 1, 1, 1, 'user');
INSERT INTO reply VALUES(4, 3, 'name3', 'null', 'message', 1, 1, 1, 'user');
INSERT INTO reply VALUES(5, 4, 'name4', 'null', 'message', 1, 1, 1, 'user');
INSERT INTO reply VALUES(6, 5, 'name5', 'null', 'message', 1, 1, 1, 'user');

UPDATE posting SET number_of_reply = 5 WHERE posting_id = 1;
