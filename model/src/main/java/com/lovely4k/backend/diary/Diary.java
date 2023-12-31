package com.lovely4k.backend.diary;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
            throw new IllegalArgumentException("you can only manage your couple's diary");
        }
    }

    public static Diary create(Integer score, LocalDate localDate, String text, Long coupleId, Sex sex, Location location) {
        validateScore(score);
        DiaryBuilder diaryBuilder = Diary.builder()
            .location(location)
            .coupleId(coupleId)
            .score(score)
            .datingDay(localDate);

        fillOutText(text, sex, diaryBuilder);
        return diaryBuilder.build();
    }

    private static void validateScore(Integer score) {
        if (score < 0 || score > 5) {
            throw new IllegalArgumentException("score out of range.");
        }
    }

    private static void fillOutText(String text, Sex sex, DiaryBuilder diaryBuilder) {
        switch (sex) {
            case MALE -> diaryBuilder.boyText(text);
            case FEMALE -> diaryBuilder.girlText(text);
            default -> throw new IllegalArgumentException("invalid input of sex");
        }
    }

    public void update(Sex sex, Integer score, LocalDate datingDay, String category, String text, List<String> uploadedImageUrls) {
        this.location.update(category);
        if (this.photos == null) {
            this.photos = new Photos();
        }
        this.photos.update(uploadedImageUrls);
        this.score = score;
        this.datingDay = datingDay;
        if (sex == Sex.MALE) {
            this.boyText = text;
        } else {
            this.girlText = text;
        }

    }
}
