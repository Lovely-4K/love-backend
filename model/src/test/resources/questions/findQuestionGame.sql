drop table if exists question;
drop table if exists question_form;
drop table if exists couple;

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

insert into couple(boy_id, girl_id) values (1, 2);