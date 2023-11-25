package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.imageuploader.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DiaryImageUploader {

    @Value("${love.image.diary-url}")
    private String basicImage;

    private final ImageUploader imageUploader;

    List<String> uploadImages(List<MultipartFile> multipartFileList) {
        if (multipartFileList == null ||  multipartFileList.isEmpty()) {
            return List.of(basicImage);
        }

        return imageUploader.upload("diary/", multipartFileList);   // NOSONAR
    }

    void deleteImages(List<String> imageUrls) {
        if (!((imageUrls.size() == 1) && (imageUrls.get(0).equals(basicImage)))) {
            imageUploader.delete("diary/", imageUrls);
        }
    }
}
