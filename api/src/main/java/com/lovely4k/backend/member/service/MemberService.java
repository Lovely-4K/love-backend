package com.lovely4k.backend.member.service;

import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.MemberupdatedEvent;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher eventPublisher;


    public MemberProfileGetResponse findMemberProfile(Long memberId) {

        Member findMember = validateMemberId(memberId);

        return MemberProfileGetResponse.of(findMember);
    }

    @Transactional
    public void updateMemberProfile(List<MultipartFile> profileImages, MemberProfileEditServiceRequest serviceRequest, Long memberId) {
        Member findMember = validateMemberId(memberId);
        String oldProfileImageUrl = findMember.getImageUrl();

        String profileImageUrl = updateProfileImage(profileImages, oldProfileImageUrl);

        updateMemberProfile(profileImageUrl, serviceRequest, findMember);
        eventPublisher.publishEvent(new MemberupdatedEvent(findMember));
    }

    private String updateProfileImage(List<MultipartFile> profileImages, String oldProfileImageUrl) {
        if (profileImages != null) {
            imageUploader.delete("member/", List.of(oldProfileImageUrl));
            checkCountOfImage(profileImages);
            return uploadImage(profileImages);
        }
        return oldProfileImageUrl;
    }

    private String uploadImage(List<MultipartFile> profileImage) {
        return imageUploader.upload("member/", profileImage).get(0);
    }

    private void checkCountOfImage(List<MultipartFile> multipartFileList) {
        if (multipartFileList.size() > 1) {
            throw new IllegalArgumentException("프로필 사진 수정시 이미지는 하나만 필요합니다.");
        }
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId)
            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 id 입니다."));
    }

    private void updateMemberProfile(String profileImageUrl, MemberProfileEditServiceRequest serviceRequest, Member findMember) {
        findMember.updateProfile(
            profileImageUrl,
            serviceRequest.nickname(),
            serviceRequest.birthday(),
            serviceRequest.mbti(),
            serviceRequest.calendarColor());
    }
}