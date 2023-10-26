package com.lovely4k.backend.diary.service.response;

import com.lovely4k.backend.diary.Photos;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PhotoList {
    private String firstImage;
    private String secondImage;
    private String thirdImage;
    private String fourthImage;
    private String fifthImage;

    @Builder
    private PhotoList(String firstImage, String secondImage, String thirdImage, String fourthImage, String fifthImage) {
        this.firstImage = firstImage;
        this.secondImage = secondImage;
        this.thirdImage = thirdImage;
        this.fourthImage = fourthImage;
        this.fifthImage = fifthImage;
    }

    public static PhotoList from(Photos photos) {
        if (photos == null) {
            return PhotoList.builder().build();
        }
        PhotoListBuilder builder = PhotoList.builder();
        int numberOfImages = photos.countOfImages();
        if (numberOfImages > 0) builder.firstImage(photos.getFirstImage());
        if (numberOfImages > 1) builder.secondImage(photos.getSecondImage());
        if (numberOfImages > 2) builder.thirdImage(photos.getThirdImage());
        if (numberOfImages > 3) builder.fourthImage(photos.getFourthImage());
        if (numberOfImages > 4) builder.fifthImage(photos.getFifthImage());
        return builder.build();
    }
}
