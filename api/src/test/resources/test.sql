
-- Couple 테이블에 데이터 삽입
INSERT INTO couple (id, boy_id, girl_id, meet_day) VALUES (1, 1, 2, '2023-01-01');

-- Member 테이블에 데이터 삽입
INSERT INTO member (id, couple_id) VALUES (1, 1);
INSERT INTO member (id, couple_id) VALUES (2, 1);

-- QuestionForm 테이블에 데이터 삽입
INSERT INTO question_form (id, member_id, question_content, first_choice, second_choice, third_choice, fourth_choice) VALUES (1, 1, 'What is your favorite color?', 'Red', 'Blue', 'Green', 'Yellow');


-- Question 테이블에 데이터 삽입
INSERT INTO question (id, couple_id, question_form_id, boy_choice_index, girl_choice_index, question_day) VALUES (1, 1, 1, 1, 1, 0);