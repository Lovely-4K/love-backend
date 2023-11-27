package com.lovely4k.backend.common.imageuploader;

import org.imgscalr.Scalr;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageResizer {

    /**
     * 이미지 크기 리사이징 유틸 클래스
     *
     * @param fileName 원본 이미지 파일의 이름
     * @param fileFormatName 원본 이미지 파일의 포맷 (예: png, jpeg...)
     * @param originalImage 원본 Multipart
     * @param targetWidth  리사이징 할 너비
     * @return 리사이징한 Multipart
     * 다음의 출처를 재구조화 했음: https://earth-95.tistory.com/129
     */
    public static MultipartFile resizeImage(String fileName, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            // MultipartFile -> BufferedImage Convert
            BufferedImage image = ImageIO.read(originalImage.getInputStream());
            // newWidth : newHeight = originWidth : originHeight
            int originWidth = image.getWidth();

            // origin 이미지가 resizing될 사이즈보다 작을 경우 resizing 작업 안 함
            if(originWidth < targetWidth)
                return originalImage;

            BufferedImage resizedImage = Scalr.resize(image, targetWidth);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, fileFormatName, byteArrayOutputStream);
            byteArrayOutputStream.flush();

            return new ResizedMultipartFile(byteArrayOutputStream.toByteArray(), fileName, originalImage.getContentType());

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 리사이즈에 실패했습니다.");
        }
    }
}