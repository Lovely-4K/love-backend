package com.lovely4k.backend.diary.service.request;

import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.location.Location;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaryCreateRequest(
        Long kakaoMapId,
        String address,
        String placeName,
        Integer score,
        LocalDate datingDay,
        BigDecimal latitude,
        BigDecimal longitude,
        String category,
        String text
) {
    public Diary toEntity(Long coupleId, Sex sex) {
        Location location = Location.create(kakaoMapId, address, placeName, latitude, longitude, category);
        return Diary.create(score, datingDay, text, coupleId, sex, location);
    }
}
