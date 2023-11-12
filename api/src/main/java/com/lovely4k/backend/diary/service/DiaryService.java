package com.lovely4k.backend.diary.service;

import com.lovely4k.backend.common.imageuploader.ImageUploader;
import com.lovely4k.backend.couple.service.IncreaseTemperatureFacade;
import com.lovely4k.backend.diary.Diary;
import com.lovely4k.backend.diary.DiaryRepositoryAdapter;
import com.lovely4k.backend.diary.Photos;
import com.lovely4k.backend.diary.service.request.DiaryCreateRequest;
import com.lovely4k.backend.diary.service.request.FillDiaryRequest;
import com.lovely4k.backend.diary.service.response.DiaryDetailResponse;
import com.lovely4k.backend.diary.service.response.DiaryListResponse;
import com.lovely4k.backend.location.Category;
import com.lovely4k.backend.member.Member;
import com.lovely4k.backend.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DiaryService {

    private final ImageUploader imageUploader;
    private final MemberRepository memberRepository;
    private final IncreaseTemperatureFacade facade;
    private final DiaryRepositoryAdapter diaryRepositoryAdapter;

    @Transactional
    public Long createDiary(List<MultipartFile> multipartFileList, DiaryCreateRequest diaryCreateRequest, Long memberId) {
        Member member = validateMemberId(memberId);
        checkCountOfImage(multipartFileList);
        List<String> uploadedImageUrls = uploadImages(multipartFileList);
        Diary diary = diaryCreateRequest.toEntity(member);
        diary.addPhoto(Photos.create(uploadedImageUrls));
        Diary savedDiary = diaryRepositoryAdapter.save(diary);
        increaseTemperature(savedDiary);

        return savedDiary.getId();
    }

    @Transactional
    public void fillDiary(Long diaryId, FillDiaryRequest serviceRequest, Long coupleId) {
        Diary diary = validateDiaryId(diaryId);
        diary.fill(coupleId, serviceRequest.text());
    }

    private void increaseTemperature(Diary savedDiary) {
        try {
            facade.increaseTemperature(savedDiary.getCoupleId());
        } catch (InterruptedException e) {  // NOSONAR
            log.warn("[System Error] Something went wrong during increasing temperature", e);
            throw new IllegalStateException("System Error Occurred", e);
        }
    }

    private Member validateMemberId(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
            () -> new EntityNotFoundException("invalid member id")
        );
    }

    private void checkCountOfImage(List<MultipartFile> multipartFileList) {
        if (multipartFileList.size() > 5) {
            throw new IllegalArgumentException("Image file can be uploaded maximum 5");
        }
    }

    private List<String> uploadImages(List<MultipartFile> multipartFileList) {
        if (multipartFileList.isEmpty()) {
            return Collections.emptyList();
        }

        return imageUploader.upload("diary/", multipartFileList);
    }

    public DiaryDetailResponse findDiaryDetail(Long diaryId, Long coupleId) {
        Diary diary = validateDiaryId(diaryId);
        diary.checkAuthority(coupleId);

        return DiaryDetailResponse.of(diary);
    }

    private Diary validateDiaryId(Long diaryId) {
        return diaryRepositoryAdapter.findById(diaryId).orElseThrow(
            () -> new EntityNotFoundException("invalid diary id")
        );
    }

    public Page<DiaryListResponse> findDiaryList(Long coupleId, Category category, Pageable pageable) {
        Page<Diary> pageDiary = diaryRepositoryAdapter.findDiaryList(coupleId, category, pageable);

        if (pageDiary.getContent().isEmpty()) {
            return Page.empty();
        }
        return pageDiary.map(DiaryListResponse::from);
    }

    @Transactional
    public void deleteDiary(Long diaryId, Long coupleId) {
        Diary diary = validateDiaryId(diaryId);
        diary.checkAuthority(coupleId);
        diaryRepositoryAdapter.delete(diary);
    }
}
