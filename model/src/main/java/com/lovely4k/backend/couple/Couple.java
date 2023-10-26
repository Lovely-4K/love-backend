package com.lovely4k.backend.couple;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
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

    @Builder
    private Couple(Long boyId, Long girlId, LocalDate meetDay, String invitationCode) {
        this.boyId = boyId;
        this.girlId = girlId;
        this.meetDay = meetDay;
        this.invitationCode = invitationCode;
    }
}
