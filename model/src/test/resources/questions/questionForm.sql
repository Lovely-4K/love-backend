DROP TABLE IF EXISTS question;
DROP TABLE IF EXISTS question_form;

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

insert into question_form(question_form_type) values ('SERVER');
insert into question_form(question_form_type) values ('CUSTOM');
insert into question_form(question_form_type) values ('SERVER');