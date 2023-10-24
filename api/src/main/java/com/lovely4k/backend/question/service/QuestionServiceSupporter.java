package com.lovely4k.backend.question.service;

import com.lovely4k.backend.common.utils.DateConverter;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.lovely4k.backend.common.ExceptionMessage.notFoundEntityMessage;

@RequiredArgsConstructor
@Component
public class QuestionServiceSupporter {
    private final CoupleRepository coupleRepository;

    /**
     * 어플에서 커플을 맺은 날짜를 기준으로 질문 날짜를 생성했다. 어플을 사용한 날짜가 앞으로도 계속 사용된다면
     * 세션에 값을 넣어 둘 생각
     * */
    @Transactional(readOnly = true)
    public long getQuestionDay(Long coupleId) {
        Couple couple = coupleRepository.findById(coupleId)
                .orElseThrow(() -> new NoSuchElementException(notFoundEntityMessage("couple", coupleId)));
        return DateConverter.getDurationOfAppUsage(couple.getLocalDateTime());
    }
}