package com.lovely4k.backend.common.imageuploader;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageUploader {
    /**
     * 이미지 업로드 시 사용
     * @param directory : image가 업로드 되어야 하는 파일 경로 디렉토리는 폴터명/으로 시작하며 /로 끝나야 한다.
     *                  e.g) "diary/"
     * @param multipartFiles : 이미지 리스트
     * @return 저장된 이미지 주소 리스트
     */
    List<String> upload(String directory, List<MultipartFile> multipartFiles);

    /**
     * 이미지 삭제 시 사용
     * @param directory : image가 업로드 되어야 하는 파일 경로 디렉토리는 폴터명/으로 시작하며 /로 끝나야 한다.
     *                  e.g) "member/"
     * @param imageUrls : AWS S3에 image 저장 후 반환된 경로 리스트
     */

    void delete(String directory, List<String> imageUrls);

}
