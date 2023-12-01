package com.lovely4k.backend.couple.service;

import com.lovely4k.backend.IntegrationTestSupport;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.couple.service.response.InvitationCodeCreateResponse;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Role;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static com.lovely4k.backend.member.Sex.FEMALE;
import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;

class CoupleServiceConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CoupleService coupleService;

    @Autowired
    CoupleRepository coupleRepository;

    @Autowired
    TaskExecutor taskExecutor;

    @DisplayName("동시에 increaseTemperature 메서드 호출이 실행되더라도 정상적으로 해결이 되어야 한다.")
    @Test
    void increaseTemperature() throws InterruptedException {
        // given
        Member boy = createMember(MALE, "ESFJ", "tommy");
        Member girl = createMember(FEMALE, "ISFP", "lisa");
        memberRepository.saveAll(List.of(boy, girl));

        // boy 와 girl1 연인 관계 생성
        InvitationCodeCreateResponse createResponse = coupleService.createInvitationCode(boy.getId(), "MALE");
        coupleService.registerCouple(createResponse.invitationCode(), girl.getId());

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Long coupleId = createResponse.coupleId();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);
        AtomicInteger failureCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < 2; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await(); // 모든 스레드가 동시에 시작하도록 대기
                    coupleService.increaseTemperature(coupleId);
                } catch (InterruptedException | ObjectOptimisticLockingFailureException e) {
                    // InterruptedException: startLatch.await()에 의해 발생할 수 있음
                    // ObjectOptimisticLockingFailureException: 동시성 예외 처리
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown(); // 스레드 시작
        doneLatch.await(); // 두 작업이 끝날 때까지 기다림
        // then
        Couple findCouple = coupleRepository.findById(coupleId).orElseThrow();
        assertThat(findCouple.getTemperature()).isEqualTo(2.0f);

    }

    private Member createMember(Sex sex, String mbti, String nickname) {
        return Member.builder()
            .sex(sex)
            .nickname(nickname)
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti(mbti)
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .role(Role.USER)
            .build();
    }


}