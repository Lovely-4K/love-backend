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
@SQLDelete(sql = "UPDATE couple SET deleted = true, deleted_date = CURRENT_DATE() WHERE id = ?")
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

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @Column(name = "deleted_date")
    private LocalDate deletedDate;
    @Builder
    private Couple(Long boyId, Long girlId, LocalDate meetDay, String invitationCode) {
        this.boyId = boyId;
        this.girlId = girlId;
        this.meetDay = meetDay;
        this.invitationCode = invitationCode;
    }

    public static Couple create(Long requestedMemberId, Sex sex, String invitationCode) {
        if (sex == Sex.MALE) {
            return createBoy(requestedMemberId, invitationCode);
        } else {
            return createGirl(requestedMemberId, invitationCode);
        }
    }

    private static Couple createGirl(Long requestedMemberId, String invitationCode) {
        return Couple.builder()
            .boyId(null)
            .girlId(requestedMemberId)
            .meetDay(null)
            .invitationCode(invitationCode)
            .build();
    }

    private static Couple createBoy(Long requestedMemberId, String invitationCode) {
        return Couple.builder()
            .boyId(requestedMemberId)
            .girlId(null)
            .meetDay(null)
            .invitationCode(invitationCode)
            .build();
    }

    public void registerGirlId(Long receivedMemberId) {
        this.girlId = receivedMemberId;
    }

    public void registerBoyId(Long receivedMemberId) {
        this.boyId = receivedMemberId;
    }

    public void update(LocalDate meetDay) {
        this.meetDay = meetDay;
    }

    public boolean hasAuthority(Long memberId) {
        return (this.boyId.equals(memberId) || this.girlId.equals(memberId));
    }
}
