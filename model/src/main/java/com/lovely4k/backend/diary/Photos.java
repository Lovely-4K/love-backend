package com.lovely4k.backend.diary;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    public int countOfImages() {
        int count = 0;
        if (this.firstImage != null) count ++;
        if (this.secondImage != null) count ++;
        if (this.thirdImage != null) count ++;
        if (this.fourthImage != null) count ++;
        if (this.fifthImage != null) count ++;
        return count;
    }

    public List<String> getPhotoList() {
        List<String> list = new ArrayList<>();
        if (this.firstImage != null) list.add(this.firstImage);
        if (this.secondImage != null) list.add(this.secondImage);
        if (this.thirdImage != null) list.add(this.thirdImage);
        if (this.fourthImage != null) list.add(this.fourthImage);
        if (this.fifthImage != null) list.add(this.fifthImage);
        return list;
    }

    public void update(List<String> uploadedImageUrls) {
        Photos updatedPhotos = create(uploadedImageUrls);
        this.firstImage = updatedPhotos.firstImage;
        this.secondImage = updatedPhotos.secondImage;
        this.thirdImage = updatedPhotos.thirdImage;
        this.fourthImage = updatedPhotos.fourthImage;
        this.fifthImage = updatedPhotos.fifthImage;
    }
}
