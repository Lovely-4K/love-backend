-- Drop tables if they exist
DROP TABLE IF EXISTS question_form;
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS location;
DROP TABLE IF EXISTS diary;
DROP TABLE IF EXISTS couple;

-- Couple 테이블
CREATE TABLE couple (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boy_id BIGINT,
    girl_id BIGINT,
    meet_day date,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couple_boy_id (boy_id),
    INDEX idx_couple_girl_id (girl_id)
);

-- Diary 테이블
CREATE TABLE diary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_id BIGINT,
    couple_id BIGINT,
    boy_text TEXT,
    girl_text TEXT,
    score INT,
    dating_day DATE,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_diary_location_id (location_id),
    INDEX idx_diary_couple_id (couple_id)
);

-- Location 테이블
CREATE TABLE location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id BIGINT,
    category VARCHAR(50),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lacation_kakao_id (kakao_id)
);

-- Member 테이블
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id BIGINT,
    sex VARCHAR(7),
    nick_name VARCHAR(50),
    birthday DATE,
    mbti VARCHAR(7),
    calendar_color VARCHAR(31),
    image_url VARCHAR(255),
    age_range VARCHAR(10),
    email VARCHAR(40),
    role VARCHAR(10),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couple_id(couple_id)
);


-- Question 테이블
CREATE TABLE question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id BIGINT,
    question_form_id BIGINT,
    boy_choice_index int,
    girl_choice_index int,
    question_day BIGINT,
    question_form_type VARCHAR(31),
    version BIGINT NOT NULL default 0,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_couple_id (couple_id),
    INDEX idx_question_question_form_id (question_form_id),
    INDEX idx_question_day (question_day)
);

-- QuestionForm 테이블
CREATE TABLE question_form (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    question_content TEXT,
    first_choice VARCHAR(255),
    second_choice VARCHAR(255),
    third_choice VARCHAR(255),
    fourth_choice VARCHAR(255),
    question_day BIGINT,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_form_member_id (member_id),
    INDEX idx_question_form_question_day (question_day)
);