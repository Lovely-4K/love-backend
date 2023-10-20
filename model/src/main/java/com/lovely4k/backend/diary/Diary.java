package com.lovely4k.backend.diary;

import com.lovely4k.backend.location.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
