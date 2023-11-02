package com.lovely4k.backend.member.service;

import com.lovely4k.backend.common.imagemanager.ImageManager;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import com.lovely4k.backend.member.service.request.MemberProfileEditServiceRequest;
import com.lovely4k.backend.member.service.response.MemberProfileGetResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ImageManager imageManager;


    public MemberProfileGetResponse findMemberProfile(Long memberId) {

        Member findMember = validateMemberId(memberId);

        return MemberProfileGetResponse.of(findMember);
    }

    @Transactional
    public void updateMemberProfile(List<MultipartFile> profileImage, MemberProfileEditServiceRequest serviceRequest, Long memberId) {
        Member findMember = validateMemberId(memberId);
        String profileImageUrl = findMember.getImageUrl();

        if (profileImage != null) {
            imageManager.delete("member/", List.of(profileImageUrl));
            checkCountOfImage(profileImage);
            profileImageUrl = uploadImage(profileImage);
        }

        updateMemberProfile(profileImageUrl, serviceRequest, findMember);
    }

    private String uploadImage(List<MultipartFile> profileImage) {
        return imageManager.upload("member/", profileImage).get(0);
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
            serviceRequest.name(),
            serviceRequest.nickname(),
            serviceRequest.birthday(),
            serviceRequest.mbti(),
            serviceRequest.calendarColor());
    }
}
