package com.lovely4k.backend.diary.service.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PhotoListResponse {
    private String firstImage;
    private String secondImage;
    private String thirdImage;
    private String fourthImage;
    private String fifthImage;

    @Builder
    private PhotoListResponse(String firstImage, String secondImage, String thirdImage, String fourthImage, String fifthImage) {
        this.firstImage = firstImage;
        this.secondImage = secondImage;
        this.thirdImage = thirdImage;
        this.fourthImage = fourthImage;
        this.fifthImage = fifthImage;
    }

}
