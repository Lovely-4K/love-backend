package com.lovely4k.backend.diary;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Photos {

    @Column(name = "first_image")
    private String firstImage;

    @Column(name = "second_image")
    private String secondImage;

    @Column(name = "third_image")
    private String thirdImage;

    @Column(name = "fourth_image")
    private String fourthImage;

    @Column(name = "fifth_image")
    private String fifthImage;

    @Builder
    private Photos(String firstImage, String secondImage, String thirdImage, String fourthImage, String fifthImage) {
        this.firstImage = firstImage;
        this.secondImage = secondImage;
        this.thirdImage = thirdImage;
        this.fourthImage = fourthImage;
        this.fifthImage = fifthImage;
    }

    public static Photos create(List<String> uploadedImageUrls) {
        if (uploadedImageUrls.isEmpty()) {
            return Photos.builder().build();
        }

        switch (uploadedImageUrls.size()) {
            case 1 -> {
                return Photos.builder()
                        .firstImage(uploadedImageUrls.get(0))
                        .build();
            }
            case 2 -> {
                return Photos.builder()
                        .firstImage(uploadedImageUrls.get(0))
                        .secondImage(uploadedImageUrls.get(1))
                        .build();
            }
            case 3 -> {
                return Photos.builder()
                        .firstImage(uploadedImageUrls.get(0))
                        .secondImage(uploadedImageUrls.get(1))
                        .thirdImage(uploadedImageUrls.get(2))
                        .build();
            }
            case 4 -> {
                return Photos.builder()
                        .firstImage(uploadedImageUrls.get(0))
                        .secondImage(uploadedImageUrls.get(1))
                        .thirdImage(uploadedImageUrls.get(2))
                        .fourthImage(uploadedImageUrls.get(3))
                        .build();
            }
            case 5 -> {
                return Photos.builder()
                        .firstImage(uploadedImageUrls.get(0))
                        .secondImage(uploadedImageUrls.get(1))
                        .thirdImage(uploadedImageUrls.get(2))
                        .fourthImage(uploadedImageUrls.get(3))
                        .fifthImage(uploadedImageUrls.get(4))
                        .build();
            }
            default -> {
                return Photos.builder().build();
            }
        }
    }
}
