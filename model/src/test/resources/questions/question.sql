-- Drop tables if they exist
DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS question_form;
DROP TABLE IF EXISTS member;
DROP TABLE IF EXISTS couple;
DROP TABLE IF EXISTS diary;
DROP TABLE IF EXISTS location;

-- Couple 테이블
CREATE TABLE couple
(
    id                     BIGINT AUTO_INCREMENT PRIMARY KEY,
    boy_id                 BIGINT,
    girl_id                BIGINT,
    meet_day               date,
    invitation_code        VARCHAR(255),
    deleted                BOOLEAN           DEFAULT FALSE,
    deleted_date           DATE,
    version                BIGINT   NOT NULL DEFAULT 0,
    temperature            FLOAT,
    couple_status          VARCHAR(30),
    re_couple_requester_id VARCHAR(30),
    created_at             datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at             datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couple_boy_id (boy_id),
    INDEX idx_couple_girl_id (girl_id),
    INDEX idx_couple_invitation_code (invitation_code)
);

-- Member 테이블
CREATE TABLE member
(
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id      BIGINT,
    sex            VARCHAR(7),
    name           VARCHAR(15),
    nick_name      VARCHAR(50),
    birthday       DATE,
    mbti           VARCHAR(7),
    calendar_color VARCHAR(31),
    image_url      VARCHAR(255),
    created_at     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_couple_id (couple_id)
);

-- Question 테이블
CREATE TABLE question
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    couple_id         BIGINT,
    question_form_id  BIGINT,
    boy_choice_index  int,
    girl_choice_index int,
    question_day      BIGINT,
    version           BIGINT   NOT NULL default 0,
    created_at        datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_couple_id (couple_id),
    INDEX idx_question_question_form_id (question_form_id),
    INDEX idx_question_day (question_day)
);

-- QuestionForm 테이블
CREATE TABLE question_form
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id          BIGINT,
    question_content   TEXT,
    first_choice       VARCHAR(255),
    second_choice      VARCHAR(255),
    third_choice       VARCHAR(255),
    fourth_choice      VARCHAR(255),
    question_day       BIGINT,
    question_form_type VARCHAR(31) NOT NULL,
    created_at         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at         datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_question_form_member_id (member_id),
    INDEX idx_question_form_question_day (question_day)
);

insert into member (couple_id, sex, image_url)
values (1, 'MALE', 'MALE PROFILE');
insert into member (couple_id, sex, image_url)
values (2, 'FEMALE', 'FEMALE PROFILE');
insert into couple (boy_id, girl_id)
values (1, 2);
insert into question_form (question_content, first_choice, second_choice, question_day, question_form_type)
values ('A vs B', 'A', 'B', 0, 'SERVER');
insert into question (couple_id, question_form_id, boy_choice_index, girl_choice_index, question_day)
values (1, 1, 1, 2, 0);