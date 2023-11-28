package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.event.Events;
import com.lovely4k.backend.couple.Couple;
import com.lovely4k.backend.couple.IncreaseTemperatureEvent;
import com.lovely4k.backend.couple.repository.CoupleRepository;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepository;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.controller.request.DiaryDeleteRequest;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.request.DiaryEditRequest;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.Sex;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final CoupleRepository coupleRepository;
    private final DiaryImageUploader diaryImageUploader;


    @Transactional
    public Long createDiary(List<MultipartFile> multipartFileList, DiaryCreateRequest diaryCreateRequest, Long memberId) {
        Member member = validateMemberId(memberId);
        checkCountOfImage(multipartFileList);
        List<String> uploadedImageUrls = diaryImageUploader.uploadImages(multipartFileList);
        Diary diary = diaryCreateRequest.toEntity(member);
        diary.addPhoto(Photos.create(uploadedImageUrls));
        Diary savedDiary = diaryRepository.save(diary);

        Events.raise(new IncreaseTemperatureEvent(member.getCoupleId()));
        return savedDiary.getId();
    }
    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new EntityNotFoundException("invalid member id")
        );
    }

    private void checkCountOfImage(List<MultipartFile> multipartFileList) {
        if (multipartFileList == null) {
            return;
        }
        if (multipartFileList.size() > 5) {
            throw new IllegalArgumentException("Image file can be uploaded maximum 5");
        }
    }

    private Diary validateDiaryId(Long diaryId) {
        return diaryRepository.findById(diaryId).orElseThrow(
                () -> new EntityNotFoundException("invalid diary id")
        );
    }

    @Transactional
    public void editDiary(Long diaryId, List<MultipartFile> multipartFileList, DiaryEditRequest request, Long coupleId, Long memberId) {
        Diary diary = validateDiaryId(diaryId);
        diary.checkAuthority(coupleId);

        List<String> imageUrls = diary.getPhotos().getPhotoList();

        diaryImageUploader.deleteImages(imageUrls);

        List<String> uploadedImageUrls = diaryImageUploader.uploadImages(multipartFileList);

        Couple couple = validateCoupleId(coupleId);
        Sex sex = getCoupleRole(memberId, couple);

        diary.update(sex, request.score(), request.datingDay(), request.category(), request.myText(), request.opponentText(), uploadedImageUrls);
    }


    private Couple validateCoupleId(Long coupleId) {
        return coupleRepository.findById(coupleId).orElseThrow(
            () -> new EntityNotFoundException("존재하는 couple이 없습니다.")
        );
    }

    private  Sex getCoupleRole(Long memberId, Couple couple) {
        Sex sex;
        if (couple.getBoyId().equals(memberId)) {
            sex = Sex.MALE;
        } else {
            sex = Sex.FEMALE;
        }
        return sex;
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long coupleId) {
        Diary diary = validateDiaryId(diaryId);
        diary.checkAuthority(coupleId);
        diaryRepository.delete(diary);
    }

    @Transactional
    public void deleteDiaries(DiaryDeleteRequest request, Long coupleId) {
        List<Diary> diaries = request.diaryList().stream().map(
            diaryId -> {
                Diary diary = validateDiaryId(diaryId);
                diary.checkAuthority(coupleId);
                return diary;
            }
        ).toList();

        diaryRepository.deleteAll(diaries);
    }
}
