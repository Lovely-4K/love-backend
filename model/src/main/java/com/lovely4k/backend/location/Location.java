package com.lovely4k.backend.location;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Location extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kakao_map_id")
    private Long kakaoMapId;

    @Column(name = "address")
    private String address;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    private Location(Long kakaoMapId, String address, Category category) {
        this.kakaoMapId = kakaoMapId;
        this.address = address;
        this.category = category;
    }

    public static Location create(Long kakaoMapId, String address, String category) {
        return new Location(kakaoMapId, address, Category.valueOf(category));
    }

    public static Location create(Long kakaoMapId, String address, Category category) {
        return new Location(kakaoMapId, address, category);
    }
}
