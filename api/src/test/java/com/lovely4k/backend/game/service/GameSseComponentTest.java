package com.lovely4k.backend.game.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.question.repository.response.FindQuestionGameResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


class GameSseComponentTest extends IntegrationTestSupport {

    @Autowired
    GameSseComponent gameSseComponent;

    @AfterEach
    void tearDown() {
        gameSseComponent.getSseEmitterMap().clear();
    }

    @Test
    @DisplayName("SseEmitter 객체를 성공적으로 생성할 수 있다.")
    void createEmitter() throws Exception {
        //given
        Long coupleId = 1L;

        //when
        SseEmitter emitter = gameSseComponent.createEmitter(coupleId);

        //then
        assertThat(emitter).isNotNull();
    }

    @Test
    @DisplayName("두명의 사용자 모두 준비가 되면 true를 반환한다.")
    void readTwoUsers() throws Exception {
        //given
        Long coupleId = 1L;
        gameSseComponent.getSseEmitterMap().put(coupleId, Arrays.asList(new SseEmitter(), new SseEmitter()));

        //when
        boolean result = gameSseComponent.readyTwoUsers(coupleId);

        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("한명의 사용자만 준비가 되면 false를 반환한다.")
    void readOneUsers() throws Exception {
        //given
        Long coupleId = 1L;
        gameSseComponent.getSseEmitterMap().put(coupleId, Arrays.asList(new SseEmitter()));

        //when
        boolean result = gameSseComponent.readyTwoUsers(coupleId);

        //then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("두명의 사용자에게 동시에 질문을 전송할 수 있다.")
    void sendQuestionToUsers() throws Exception {
        //given
        Long coupleId = 1L;
        SseEmitter mockEmitter1 = mock(SseEmitter.class);
        SseEmitter mockEmitter2 = mock(SseEmitter.class);

        gameSseComponent.getSseEmitterMap().put(coupleId, Arrays.asList(mockEmitter1, mockEmitter2));

        FindQuestionGameResponse response = new FindQuestionGameResponse(
            "testQuestionContent",
            "testFirstChoice",
            "testSecondChoice",
            "testThirdChoice",
            "testFourthChoice",
            1,
            2
        );

        //when
        gameSseComponent.sendQuestionToUsers(coupleId, response);

        //then
        verify(mockEmitter1, times(1)).send(any(SseEmitter.SseEventBuilder.class));
        verify(mockEmitter2, times(1)).send(any(SseEmitter.SseEventBuilder.class));
    }
}