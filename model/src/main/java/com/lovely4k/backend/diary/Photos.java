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
        if (uploadedImageUrls == null || uploadedImageUrls.isEmpty()) {
            return Photos.builder().build();
        }

        PhotosBuilder builder = Photos.builder();
        setImages(builder, uploadedImageUrls);

        return builder.build();
    }

    private static void setImages(PhotosBuilder builder, List<String> uploadedImageUrls) {
        int size = uploadedImageUrls.size();
        if (size > 0) builder.firstImage(uploadedImageUrls.get(0));
        if (size > 1) builder.secondImage(uploadedImageUrls.get(1));
        if (size > 2) builder.thirdImage(uploadedImageUrls.get(2));
        if (size > 3) builder.fourthImage(uploadedImageUrls.get(3));
        if (size > 4) builder.fifthImage(uploadedImageUrls.get(4));
    }
}
