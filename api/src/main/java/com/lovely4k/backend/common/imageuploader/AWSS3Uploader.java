package com.lovely4k.backend.common.imageuploader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3Uploader implements ImageUploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private static final String DEFAULT_DIRECTORY = "images/";

    @Override
    public List<String> upload(String directory, List<MultipartFile> multipartFiles) {
        if (multipartFiles.isEmpty()) {
            return List.of();
        }

        List<String> uploadedImageUrl = new ArrayList<>();
        multipartFiles.forEach(
            multipartFile -> {
                String originalFilename = multipartFile.getOriginalFilename();

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(multipartFile.getSize());
                objectMetadata.setContentType(multipartFile.getContentType());

                try {
                    amazonS3.putObject(bucket, DEFAULT_DIRECTORY + directory + originalFilename, multipartFile.getInputStream(), objectMetadata);
                } catch (IOException e) {
                    log.warn("[Warning] Image Upload to S3 has some exception", e);
                    throw new AmazonServiceException("Some Error occurred during Upload Image to S3 Server", e);
                }
                uploadedImageUrl.add(convertURL(amazonS3.getUrl(bucket, originalFilename).toString(), directory));
            }
        );
        return uploadedImageUrl;
    }

    // 원하는 부분을 찾아 새 경로를 추가
    public static String convertURL(String url, String directory) {

        String searchPattern = ".com/";
        int index = url.indexOf(searchPattern) + searchPattern.length();
        String newPart = DEFAULT_DIRECTORY + directory;

        return url.substring(0, index) + newPart + url.substring(index);
    }

    @Override
    public void delete(String directory, List<String> imageUrls) {
        String splitStr = ".com/";
        imageUrls.stream()
            .map(imageUrl -> imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length()))
            .map(fileName -> new DeleteObjectRequest(bucket, fileName))
            .forEach(amazonS3::deleteObject);
    }

}
