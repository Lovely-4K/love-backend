package com.lovely4k.backend.common.imageuploader;

import com.lovely4k.backend.IntegrationTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AWSS3UploaderTest extends IntegrationTestSupport {

    @Autowired
    ImageUploader imageUploader;

    @Disabled(value = "본 테스트의 경우 AWS S3 테스트를 하고자 할 때 사용하면 됩니다.")
    @DisplayName("upload method를 통해 이미지를 업로드 할 수 있다.")
    @Test
    void upload() {
        // given
        MockMultipartFile mockMultipartFile =
                new MockMultipartFile("images", "image1.png", "image/png", "some-image".getBytes());

        // when
        List<String> uploadedImageUrls = imageUploader.upload("test/", List.of(mockMultipartFile));

        // then
        assertThat(uploadedImageUrls).isNotEmpty();
    }


}
