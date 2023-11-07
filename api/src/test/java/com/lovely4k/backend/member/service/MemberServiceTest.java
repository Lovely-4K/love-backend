package com.lovely4k.backend.member.service;

import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.lovely4k.backend.member.Sex.MALE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    ImageUploader imageUploader;

    @Test
    @DisplayName("회원 정보를 조회한다.")
    void getMemberProfile() throws Exception {
        //given
        Member member = createMember();

        Member savedMember = memberRepository.save(member);

        //when
        MemberProfileGetResponse memberProfileGetResponse = memberService.findMemberProfile(savedMember.getId());

        //then
        assertThat(memberProfileGetResponse)
            .extracting("imageUrl", "name")
            .contains("http://www.imageUrlSample.com", "김철수");
    }

    @Test
    @DisplayName("존재하지 않는 id로 프로필을 조회할 경우 예외가 발생한다.")
    void getMemberProfileByWrongId() throws Exception {
        //given
        Member member = createMember();

        memberRepository.save(member);

        //when && then
        assertThatThrownBy(() -> memberService.findMemberProfile(100L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("회원의 프로필 정보를 수정할 수 있다.")
    void updateMemberProfile() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        MemberProfileEditServiceRequest serviceRequest = new MemberProfileEditServiceRequest(
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        MockMultipartFile profileImage = new MockMultipartFile("images", "newProfileImage.png", "image/png", "some-image".getBytes());
        List<MultipartFile> multipartFileList = List.of(profileImage);

        given(imageUploader.upload(any(String.class), any())
        ).willReturn(List.of("profile-image-url"));

        //when
        memberService.updateMemberProfile(multipartFileList, serviceRequest, savedMember.getId());

        //then
        Member updatedMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id 입니다."));

        assertThat(updatedMember).extracting("name", "nickname", "birthday", "imageUrl")
            .contains("김동수", "길쭉이", LocalDate.of(1996, 7, 31), "profile-image-url");
    }

    @Test
    @DisplayName("프로필 수정 시 두개 이상의 이미지를 요청하면 에러가 발생한다.")
    void updateMemberProfileWithMoreThanOneImage() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);
        Long savedMemberId = savedMember.getId();

        MemberProfileEditServiceRequest serviceRequest = new MemberProfileEditServiceRequest(
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        MockMultipartFile profileImage1 = new MockMultipartFile("images", "newProfileImage1.png", "image/png", "some-image".getBytes());
        MockMultipartFile profileImage2 = new MockMultipartFile("images", "newProfileImage2.png", "image/png", "some-image".getBytes());
        List<MultipartFile> multipartFileList = List.of(profileImage1, profileImage2);

        //when & then
        assertThatThrownBy(() -> memberService.updateMemberProfile(multipartFileList, serviceRequest, savedMemberId))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("프로필 수정 시 이미지가 없는 경우 기존 imageUrl이 유지된다.")
    void updateMemberProfileWithoutImage() throws Exception {
        //given
        Member member = createMember();
        Member savedMember = memberRepository.save(member);

        MemberProfileEditServiceRequest serviceRequest = new MemberProfileEditServiceRequest(
            "김동수",
            "길쭉이",
            LocalDate.of(1996, 7, 31),
            "ENFP",
            "blue"
        );

        //when
        memberService.updateMemberProfile(null, serviceRequest, savedMember.getId());

        //then
        Member updatedMember = memberRepository.findById(savedMember.getId())
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id 입니다."));

        assertThat(updatedMember).extracting("name", "nickname", "birthday", "imageUrl")
            .contains("김동수", "길쭉이", LocalDate.of(1996, 7, 31), "http://www.imageUrlSample.com");
    }

    private Member createMember() {
        return Member.builder()
            .coupleId(1L)
            .sex(MALE)
            .name("김철수")
            .nickname("듬직이")
            .birthday(LocalDate.of(1996, 7, 30))
            .mbti("ESFJ")
            .calendarColor("white")
            .imageUrl("http://www.imageUrlSample.com")
            .build();
    }
}
