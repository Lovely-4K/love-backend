package com.lovely4k.backend.location;

import com.lovely4k.backend.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "latitude", precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 11, scale = 7)
    private BigDecimal longitude;

    @Enumerated(value = EnumType.STRING)
    private Category category;

    public Location(Long kakaoMapId, String address, String placeName, BigDecimal latitude, BigDecimal longitude, Category category) {
        this.kakaoMapId = kakaoMapId;
        this.address = address;
        this.placeName = placeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
    }

    public static Location create(Long kakaoMapId, String address, String placeName, BigDecimal latitude, BigDecimal longitude, String category) {
        return new Location(kakaoMapId, address, placeName, latitude, longitude, Category.valueOf(category));
    }

    public static Location create(Long kakaoMapId, String address, String placeName, BigDecimal latitude, BigDecimal longitude, Category category) {
        return new Location(kakaoMapId, address, placeName, latitude, longitude, category);
    }
}
