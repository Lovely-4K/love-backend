package com.lovely4k.backend.common.imageuploader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3Uploader implements ImageUploader {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;

    private static final String DEFAULT_DIRECTORY = "images/";

    private final TaskExecutor executor;

    @Override
    public List<String> upload(String directory, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return List.of();
        }

        List<CompletableFuture<String>> futures = multipartFiles.stream()
            .map(multipartFile -> CompletableFuture.supplyAsync(() -> uploadProcess(directory, multipartFile), executor))
            .toList();

        return gatherFileNamesFromFutures(directory, futures);
    }

    private String uploadProcess(String directory, MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String contentType = Objects.requireNonNull(multipartFile.getContentType());
        String fileFormatName = contentType.substring(contentType.lastIndexOf("/") + 1);

        MultipartFile resizedFile = ImageResizer.resizeImage(originalFilename, fileFormatName, multipartFile, 1920);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(resizedFile.getSize());
        objectMetadata.setContentType(resizedFile.getContentType());

        try {
            amazonS3.putObject(bucket, DEFAULT_DIRECTORY + directory + originalFilename, resizedFile.getInputStream(), objectMetadata);
        } catch (IOException e) {
            throw new AmazonServiceException("Some Error occurred during Upload Image to S3 Server", e);
        }
        return convertURL(amazonS3.getUrl(bucket, originalFilename).toString(), directory);
    }

    private List<String> gatherFileNamesFromFutures(String directory, List<CompletableFuture<String>> futures) {
        List<String> fileNames = new ArrayList<>();
        AtomicBoolean catchException = new AtomicBoolean(false);
        futures.forEach(future -> {
            try {
                fileNames.add(future.join());
            } catch (CompletionException e) {
                catchException.set(true);
            }
        });
        handleException(directory, catchException, fileNames);
        return fileNames;
    }

    //이미지 업로드 중 예외 발생시 이미지 다시 삭제
    private void handleException(String directory, AtomicBoolean catchException, List<String> fileNames) {
        if (catchException.get()) {
            executor.execute(() -> delete(directory, fileNames));
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드 예외 발생.");
        }
    }


    // 원하는 부분을 찾아 새 경로를 추가
    public static String convertURL(String url, String directory) {

        String searchPattern = ".com/";
        int index = url.indexOf(searchPattern) + searchPattern.length();
        String newPart = DEFAULT_DIRECTORY + directory;

        return url.substring(0, index) + newPart + url.substring(index);
    }

    //삭제는 자주 일어나지 않을 것이라고 생각이 들어 ForkJoinPool을 사용하는 병렬 스트림 이용
    @Override
    public void delete(String directory, List<String> imageUrls) {
        String splitStr = ".com/";
        imageUrls.stream()
            .parallel()
            .map(imageUrl -> imageUrl.substring(imageUrl.lastIndexOf(splitStr) + splitStr.length()))
            .map(fileName -> new DeleteObjectRequest(bucket, fileName))
            .forEach(amazonS3::deleteObject);
    }

}