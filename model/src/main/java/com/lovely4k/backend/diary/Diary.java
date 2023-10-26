package com.lovely4k.backend.diary;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "location_id")
    private Location location;

    @Column(name = "couple_id")
    private Long coupleId;

    @Lob
    @Column(name = "boy_text")
    private String boyText;

    @Lob
    @Column(name = "girl_text")
    private String girlText;

    @Column(name = "score")
    private Integer score;

    @Column(name = "dating_day")
    private LocalDate datingDay;

    @Embedded
    private Photos photos;

    @Builder
    private Diary(Location location, Long coupleId, String boyText, String girlText, Integer score, LocalDate datingDay, Photos photos) {
        this.location = location;
        this.coupleId = coupleId;
        this.boyText = boyText;
        this.girlText = girlText;
        this.score = score;
        this.datingDay = datingDay;
        this.photos = photos;
    }

    public void addPhoto(Photos photos) {
        this.photos = photos;
    }

    public void checkAuthority(Long coupleId) {
        if (!this.coupleId.equals(coupleId)) {
            throw new IllegalArgumentException("you can only see your couple's diary");
        }
    }

    public static Diary create(Integer score, LocalDate localDate, String text, Member member, Location location) {
        validateScore(score);
        DiaryBuilder diaryBuilder = Diary.builder()
                .location(location)
                .coupleId(member.getCoupleId())
                .score(score)
                .datingDay(localDate);

        fillOutText(text, member, diaryBuilder);
        return diaryBuilder.build();
    }

    private static void validateScore(Integer score) {
        if (score < 0 || score > 5) {
            throw new IllegalArgumentException("score out of range.");
        }
    }

    private static void fillOutText(String text, Member member, DiaryBuilder diaryBuilder) {
        switch (member.getSex()) {
            case "boy" -> diaryBuilder.boyText(text);
            case "girl" -> diaryBuilder.girlText(text);
            default -> throw new IllegalArgumentException("invalid input of sex");
        }
    }
}
