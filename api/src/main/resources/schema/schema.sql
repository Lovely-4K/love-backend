-- Drop tables if they exist
DROP TABLE IF EXISTS QuestionForm;
DROP TABLE IF EXISTS Question;
DROP TABLE IF EXISTS Member;
DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS Diary;
DROP TABLE IF EXISTS Couple;

-- Couple 테이블
CREATE TABLE Couple (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    boy_id BIGINT,
    girl_id BIGINT,
    meet_day DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL,
    INDEX idx_couple_boy_id (boy_id),
    INDEX idx_couple_girl_id (girl_id)
);

-- Diary 테이블
CREATE TABLE Diary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    location_id BIGINT,
    couple_id BIGINT,
    boy_text TEXT,
    girl_text TEXT,
    score INT,
    dating_day DATE,
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL,
    INDEX idx_diary_location_id (location_id),
    INDEX idx_diary_couple_id (couple_id)
);

-- Location 테이블
CREATE TABLE Location (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    kakao_id BIGINT,
    category VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL,
    INDEX idx_lacation_kakao_id (kakao_id)
);

-- Member 테이블
CREATE TABLE Member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id BIGINT,
    sex VARCHAR(7),
    name VARCHAR(15),
    nick_name VARCHAR(50),
    birthday DATE,
    mbti VARCHAR(7),
    calendar_color VARCHAR(31),
    image_url VARCHAR,
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL
);

-- Question 테이블
CREATE TABLE Question (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id BIGINT,
    question_form_id BIGINT,
    boy_answer TEXT,
    girl_answer TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL,
    INDEX idx_question_couple_id (couple_id),
    INDEX idx_question_question_form_id (question_form_id)
);

-- QuestionForm 테이블
CREATE TABLE QuestionForm (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT,
    questionContent TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at DATE NOT NULL,
    INDEX idx_question_form_member_id (member_id)
);
