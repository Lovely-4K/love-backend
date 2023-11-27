package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.event.Events;
import com.lovely4k.backend.common.imageuploader.ImageUploader;
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

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final CoupleRepository coupleRepository;
    private final ImageUploader imageUploader;


    @Transactional
    public Long createDiary(List<MultipartFile> multipartFileList, DiaryCreateRequest diaryCreateRequest, Long memberId) {
        Member member = validateMemberId(memberId);
        checkCountOfImage(multipartFileList);
        List<String> uploadedImageUrls = uploadImages(multipartFileList);
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
        List<String> editedImageUrls = new ArrayList<>();

        addRemainImages(request, editedImageUrls);
        checkImages(editedImageUrls, multipartFileList);
        addUploadedImages(multipartFileList, editedImageUrls);
        deleteImageFromS3(request, diary);
        Sex sex = getCoupleRole(memberId, validateCoupleId(coupleId));

        diary.update(sex, request.score(), request.datingDay(), request.category(), request.text(), editedImageUrls);
    }

    private void addRemainImages(DiaryEditRequest request, List<String> editedImageUrls) {
        if (request.images() != null) {
            editedImageUrls.addAll(request.images());
        }
    }

    private void addUploadedImages(List<MultipartFile> multipartFileList, List<String> editedImageUrls) {
        editedImageUrls.addAll(uploadImages(multipartFileList));
    }

    private void checkImages(List<String> editedImageUrls, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            return;
        }
      
        if (editedImageUrls.size() + multipartFiles.size() > 5) {
            throw new IllegalArgumentException("이미지는 최대 5개를 넘길 수 없습니다.");
        }
    }

    private void deleteImageFromS3(DiaryEditRequest request, Diary diary) {
        if (request.images() != null) {
            if (diary.getPhotos() == null) {
                throw new IllegalArgumentException("잘못된 요청입니다. 당사의 API docs를 다시 읽어주세요.");
            }
            deleteImages(diary.getPhotos().getPhotoList()
                .stream().filter(
                    imageUrl -> !request.images().contains(imageUrl)
                ).toList());
        } else {
            if (diary.getPhotos() != null) {
                deleteImages(diary.getPhotos().getPhotoList());
            }
        }
    }


    private Couple validateCoupleId(Long coupleId) {
        return coupleRepository.findById(coupleId).orElseThrow(
            () -> new EntityNotFoundException("존재하는 couple이 없습니다.")
        );
    }

    private Sex getCoupleRole(Long memberId, Couple couple) {
        if (couple.getBoyId().equals(memberId)) {
            return Sex.MALE;
        } else {
            return Sex.FEMALE;
        }
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

    private List<String> uploadImages(List<MultipartFile> multipartFileList) {
        return imageUploader.upload("diary/", multipartFileList);   // NOSONAR
    }

    private void deleteImages(List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        imageUploader.delete("diary/", imageUrls);
    }
}