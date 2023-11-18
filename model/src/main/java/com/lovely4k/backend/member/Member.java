package com.lovely4k.backend.member;

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
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "couple_id")
    private Long coupleId;

    @Column(length = 7, name = "sex")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "nick_name")
    private String nickname;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(length = 7, name = "mbti")
    private String mbti;

    @Column(length = 31, name = "calendar_color")
    private String calendarColor;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "age_range")
    private String ageRange;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    private Member(Long coupleId, Sex sex, String nickname, LocalDate birthday, String mbti, String calendarColor, String imageUrl, String ageRange, String email, Role role) { // NOSONAR
        this.coupleId = coupleId;
        this.sex = sex;
        this.nickname = nickname;
        this.birthday = birthday;
        this.mbti = mbti;
        this.calendarColor = calendarColor;
        this.imageUrl = imageUrl;
        this.ageRange = ageRange;
        this.email = email;
        this.role = role;
    }

    public void
    updateProfile(String imageUrl, String nickname, LocalDate birthday, String mbti, String calendarColor) {
        this.imageUrl = imageUrl;
        this.nickname = nickname;
        this.birthday = birthday;
        this.mbti = mbti;
        this.calendarColor = calendarColor;
    }

    public void registerCoupleId(Long id) {
        this.coupleId = id;
    }

    public Member update(String ageRange) {
        this.ageRange = ageRange;
        return this;
    }

    public void checkReCoupleCondition(Long coupleId) {
        if (!this.coupleId.equals(coupleId)) {
            throw new IllegalArgumentException("상대방은 커플 재결합을 할 수 있는 상태가 아닙니다.");
        }
    }

//    @Override
//    public String toString() {
//        return "Member{" +
//            "id=" + id +
//            ", coupleId=" + coupleId +
//            ", sex=" + sex +
//            ", nickname='" + nickname + '\'' +
//            ", birthday=" + birthday +
//            ", mbti='" + mbti + '\'' +
//            ", calendarColor='" + calendarColor + '\'' +
//            ", imageUrl='" + imageUrl + '\'' +
//            ", ageRange='" + ageRange + '\'' +
//            ", email='" + email + '\'' +
//            ", role=" + role +
//            '}';
//    }
}