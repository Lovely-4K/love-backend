package com.lovely4k.backend.common.error.exception;

public class QuestionCreateCountExceedException extends RuntimeException{
    public QuestionCreateCountExceedException() {
        super("질문 생성 횟수가 허용된 한도를 초과하였습니다.");
    }
}