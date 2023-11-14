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
    invitation_code VARCHAR(255),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_date DATE,
    version BIGINT NOT NULL DEFAULT 0,
    temperature FLOAT,
    couple_status VARCHAR(30),
    re_couple_requester_id VARCHAR(30),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couple_boy_id (boy_id),
    INDEX idx_couple_girl_id (girl_id),
    INDEX idx_couple_invitation_code (invitation_code)
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
    first_image VARCHAR(255),
    second_image VARCHAR(255),
    third_image VARCHAR(255),
    fourth_image VARCHAR(255),
    fifth_image VARCHAR(255),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_diary_location_id (location_id),
    INDEX idx_diary_couple_id (couple_id)
);

-- Location 테이블
CREATE TABLE location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_map_id BIGINT,
    address VARCHAR(255),
    category VARCHAR(50),
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_lacation_kakao_map_id (kakao_map_id)
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
    question_form_type VARCHAR(31) NOT NULL ,
    created_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_form_member_id (member_id),
    INDEX idx_question_form_question_day (question_day)
);

CREATE TABLE calendar(
     id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
     start_date DATE NOT NULL,
     end_date DATE,
     member_id BIGINT,
     couple_id BIGINT NOT NULL,
     schedule_type VARCHAR(31) NOT NULL,
     schedule_details VARCHAR(255) NOT NULL,
     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
     INDEX idx_calendar_member_id (member_id),
     INDEX idx_calendar_start_date (start_date),
     INDEX idx_calendar_end_Date (end_date),
     INDEX idx_calendar_couple_id (couple_id)
);