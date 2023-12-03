package com.lovely4k.backend.couple;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import com.lovely4k.backend.member.Sex;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SQLDelete(sql = "UPDATE couple SET deleted = true, deleted_date = CURRENT_DATE, couple_status = 'BREAKUP' WHERE id = ? and version = ?")
@Where(clause = "deleted = false")
public class Couple extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "boy_id")
    private Long boyId;

    @Column(name = "girl_id")
    private Long girlId;

    @Column(name = "meet_day")
    private LocalDate meetDay;

    @Column(name = "invitation_code")
    private String invitationCode;

    @Column(name = "temperature")
    private Float temperature;

    @Version
    private Long version;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @Column(name = "deleted_date")
    private LocalDate deletedDate;

    @Column(name = "couple_status")
    @Enumerated(value = EnumType.STRING)
    private CoupleStatus coupleStatus;

    @Column(name = "re_couple_requester_id")
    private Long reCoupleRequesterId;


    @Builder
    private Couple(Long boyId, Long girlId, LocalDate meetDay, String invitationCode, Float temperature, Long version, boolean deleted, LocalDate deletedDate, CoupleStatus coupleStatus, Long reCoupleRequesterId) {   // NOSONAR
        this.boyId = boyId;
        this.girlId = girlId;
        this.meetDay = meetDay;
        this.invitationCode = invitationCode;
        this.temperature = temperature;
        this.version = version;
        this.deleted = deleted;
        this.deletedDate = deletedDate;
        this.coupleStatus = coupleStatus;
        this.reCoupleRequesterId = reCoupleRequesterId;
    }

    public static Couple create(Long requestedMemberId, Sex sex, String invitationCode) {
        if (sex == Sex.MALE) {
            return createBoy(requestedMemberId, invitationCode);
        } else {
            return createGirl(requestedMemberId, invitationCode);
        }
    }

    private static Couple createBoy(Long requestedMemberId, String invitationCode) {
        return Couple.builder()
            .boyId(requestedMemberId)
            .girlId(null)
            .meetDay(null)
            .invitationCode(invitationCode)
            .temperature(0.0f)
            .coupleStatus(CoupleStatus.SOLO)
            .build();
    }

    private static Couple createGirl(Long requestedMemberId, String invitationCode) {
        return Couple.builder()
            .boyId(null)
            .girlId(requestedMemberId)
            .meetDay(null)
            .invitationCode(invitationCode)
            .temperature(0.0f)
            .coupleStatus(CoupleStatus.SOLO)
            .build();
    }

    public void update(LocalDate meetDay) {
        this.meetDay = meetDay;
    }

    /*
     * Demo day 이후 사랑의 온도 비중 얘기 나눈 후 비중 관련 Calculator 추가 예정
     * 현재는 단순하게 건당 +1 도
     * 최소 온도는 0도, 최대 온도는 100도
     */
    public void increaseTemperature() {
        if (this.temperature >= 100f) {
            this.temperature = 100f;
        } else {
            this.temperature += 1f;
        }
    }

    public boolean hasAuthority(Long memberId) {
        return (this.boyId.equals(memberId) || this.girlId.equals(memberId));
    }

    public void registerPartnerId(Long receivedMemberId) {
        if (this.boyId == null) {
            this.boyId = receivedMemberId;
        } else {
            this.girlId = receivedMemberId;
        }
        this.coupleStatus = CoupleStatus.RELATIONSHIP;
        this.meetDay = LocalDate.now();
    }
  
    public boolean isExpired(LocalDate requestedDate) {
        LocalDate limitedDate = this.deletedDate.plusDays(30);
        return requestedDate.isAfter(limitedDate);
    }

    public void recouple(Long memberId, LocalDate requestedDate) {
        checkCoupleStatus();
        checkAuthority(memberId);

        if (this.coupleStatus == CoupleStatus.BREAKUP) {  // 요청을 하는 경우
            checkExpired(requestedDate);
            this.coupleStatus = CoupleStatus.RECOUPLE;
            this.reCoupleRequesterId = memberId;
        } else if (coupleStatus == CoupleStatus.RECOUPLE){    // 받은 요청을 수락하는 경우
            checkInvalidAccess(memberId);
            this.deleted = false;
            this.deletedDate = null;
            this.coupleStatus = CoupleStatus.RELATIONSHIP;
            this.reCoupleRequesterId = null;
        }
    }

    private void checkCoupleStatus() {
        if (this.coupleStatus == CoupleStatus.RELATIONSHIP) {
            throw new IllegalStateException("현재 커플의 경우 재결합을 할 수 있는 상태가 아닙니다.");
        }
    }

    public void checkAuthority(Long memberId) {
        if (!hasAuthority(memberId)) {
            throw new IllegalArgumentException(String.format("member %d은 couple %d에 대한 권한이 없습니다.", memberId, id));
        }
    }

    private void checkExpired(LocalDate requestedDate) {
        if (isExpired(requestedDate)) {
            throw new IllegalStateException("커플을 끊은 지 30일이 지났기 때문에 복원을 할 수 없습니다.");
        }
    }

    private void checkInvalidAccess(Long memberId) {
        if (this.reCoupleRequesterId.equals(memberId)) {
            throw new IllegalArgumentException("재결합 신청한 요청자는 재결합을 수락할 수 없습니다.");
        }
    }

    public boolean isRecoupleReceiver(Long memberId) {
        return hasAuthority(memberId) && this.coupleStatus == CoupleStatus.RECOUPLE && !this.reCoupleRequesterId.equals(memberId);
    }

    public Long getOpponentId(Long memberId) {
        if (this.boyId.equals(memberId)) {
            return this.girlId;
        } else {
            return this.boyId;
        }
    }

    public Sex getCoupleRole(Long memberId) {
        if (this.boyId.equals(memberId)) {
            return Sex.MALE;
        } else {
            return Sex.FEMALE;
        }
    }
}
