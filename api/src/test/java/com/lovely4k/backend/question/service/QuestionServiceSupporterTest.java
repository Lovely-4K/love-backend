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
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(5);
        Couple couple = mock(Couple.class);

        given(couple.getLocalDateTime()).willReturn(localDateTime);
        given(coupleRepository.findById(coupleId)).willReturn(Optional.of(couple));

        // When
        long questionDay = questionServiceSupporter.getQuestionDay(coupleId);

        // Then
        assertThat(questionDay).isEqualTo(DateConverter.getDurationOfAppUsage(localDateTime));
    }
}