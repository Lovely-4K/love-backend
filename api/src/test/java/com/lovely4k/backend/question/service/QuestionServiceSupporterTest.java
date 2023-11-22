package com.lovely4k.backend.question.service;

import com.lovely4k.backend.common.utils.DateConverter;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class QuestionServiceSupporterTest {
    @Mock
    private CoupleRepository coupleRepository;

    @InjectMocks
    private QuestionServiceSupporter questionServiceSupporter;

    @DisplayName("질문 날짜 가져오기")
    @Test
    void getQuestionDay_ValidCase() {
        // Given
        Long coupleId = 1L;
        LocalDateTime localDate = LocalDateTime.now().minusDays(5);
        Couple couple = mock(Couple.class);

        given(couple.getCreatedDate()).willReturn(localDate);
        given(coupleRepository.findById(coupleId)).willReturn(Optional.of(couple));

        // When
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);

        // Then
        assertThat(questionDay).isEqualTo(DateConverter.getDurationOfAppUsage(localDate));
    }

    @DisplayName("커플 id를 받아서 남자로 등록되어 있는 회원의 id 가져오기")
    @Test
    void getBoyId() {
        //given
        Long coupleId = 1L;
        Couple couple = mock(Couple.class);
        Long expected = 1L;
        given(coupleRepository.findById(coupleId)).willReturn(Optional.of(couple));
        given(couple.getBoyId()).willReturn(expected);

        //when
        Long actual = questionServiceSupporter.getBoyId(coupleId);

        //then
        assertThat(expected).isEqualTo(actual);
    }
}