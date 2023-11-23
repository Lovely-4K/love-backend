package com.lovely4k.backend.diary.service.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.member.Sex;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DiaryDetailResponse(
    Long kakaoMapId,
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate datingDay,
    Integer score,
    Category category,
    String myText,
    String opponentText,
    PhotoListResponse pictures,
    String placeName,
    BigDecimal latitude,
    BigDecimal longitude

) {
    public static DiaryDetailResponse of(Diary diary, Sex sex) {
        if (sex == Sex.MALE) {
            return new DiaryDetailResponse(diary.getLocation().getKakaoMapId(), diary.getDatingDay(), diary.getScore(), diary.getLocation().getCategory(),
                diary.getBoyText(), diary.getGirlText(),
                PhotoListResponse.from(diary.getPhotos()),
                diary.getLocation().getPlaceName(), diary.getLocation().getLatitude(), diary.getLocation().getLongitude());
        } else {
            return new DiaryDetailResponse(diary.getLocation().getKakaoMapId(), diary.getDatingDay(), diary.getScore(), diary.getLocation().getCategory(),
                diary.getGirlText(), diary.getBoyText(),
                PhotoListResponse.from(diary.getPhotos()),
                diary.getLocation().getPlaceName(), diary.getLocation().getLatitude(), diary.getLocation().getLongitude());
        }
    }
}
